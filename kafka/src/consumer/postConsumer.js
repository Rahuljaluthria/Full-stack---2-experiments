const {
    isAlreadyProcessed,
    markAsProcessed
} = require("../utils/idempotency");
const kafka = require("../config/kafka");

const consumer = kafka.consumer({
    groupId: "scheduled-post-consumer-group",
});

const dlqProducer = kafka.producer();

const MAX_RETRIES = 3;
const RETRY_DELAY = 1000;

async function processPost(post) {
    console.log(`Processing post: ${post.postId}`);

    if (isAlreadyProcessed(post.eventId)) {
        console.log("Duplicate event detected. Skipping...");
        return;
    }

    if (post.simulateFailure === true) {
        throw new Error("Simulated publishing service failure");
    }

    // Simulate successful publishing
    console.log("Post published successfully!");

    markAsProcessed(post.eventId);
}

async function sendToDLQ(post, error, metadata) {
    const failedEvent = {
        originalEvent: post,
        eventId: post.eventId,
        failureReason: error.message,
        retryAttempts: MAX_RETRIES,
        failedAt: new Date().toISOString(),
        originalTopic: metadata.topic,
        originalPartition: metadata.partition,
        originalOffset: metadata.offset,
    };

    await dlqProducer.send({
        topic: "scheduled-posts-dlq",
        messages: [
            {
                key: post.eventId,
                value: JSON.stringify(failedEvent),
            },
        ],
    });

    console.log("Message successfully moved to DLQ!");
    console.log(failedEvent);
}

async function consumePosts() {
    try {
        await dlqProducer.connect();
        await consumer.connect();

        console.log("Consumer connected to Kafka");

        await consumer.subscribe({
            topic: "scheduled-posts",
            fromBeginning: true,
        });

        console.log("Waiting for scheduled posts...\n");

        await consumer.run({
            autoCommit: false,

            eachMessage: async ({
                topic,
                partition,
                message,
            }) => {
                const post = JSON.parse(message.value.toString());

                let successful = false;
                let lastError;

                for (let attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                    try {
                        console.log(
                            `Attempt ${attempt}/${MAX_RETRIES} for ${post.postId}`
                        );

                        await processPost(post);

                        successful = true;
                        break;

                    } catch (error) {
                        lastError = error;

                        console.log(
                            `Attempt ${attempt} failed: ${error.message}`
                        );

                        if (attempt < MAX_RETRIES) {
                            console.log("Retrying in 1 second...\n");

                            await new Promise(resolve =>
                                setTimeout(resolve, RETRY_DELAY)
                            );
                        }
                    }
                }

                if (successful) {
                    console.log("Event processed successfully!");
                } else {
                    console.log("All retry attempts failed.");
                    console.log("Sending event to DLQ...");

                    // Send failed event before committing
                    await sendToDLQ(post, lastError, {
                        topic,
                        partition,
                        offset: message.offset,
                    });
                }

                // Commit only after success OR confirmed DLQ send
                await consumer.commitOffsets([{
                    topic,
                    partition,
                    offset: (
                        BigInt(message.offset) + 1n
                    ).toString(),
                }]);
            },
        });

    } catch (error) {
        console.error("Consumer error:", error);
    }
}

consumePosts();
