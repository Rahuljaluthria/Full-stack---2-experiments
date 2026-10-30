
const kafka = require("../config/kafka");

const consumer = kafka.consumer({
    groupId: "scheduled-post-consumer-group",
});

const MAX_RETRIES = 3;
const RETRY_DELAY = 1000;

async function processPost(post) {
    console.log(`Processing post: ${post.postId}`);

    // Simulate a processing failure for testing
    if (post.simulateFailure === true) {
        throw new Error("Simulated publishing service failure");
    }

    console.log("Post published successfully!");
}

async function consumePosts() {
    try {
        await consumer.connect();

        console.log("Consumer connected to Kafka");

        await consumer.subscribe({
            topic: "scheduled-posts",
            fromBeginning: true,
        });

        await consumer.run({
            autoCommit: false,

            eachMessage: async ({ topic, partition, message }) => {
                const post = JSON.parse(message.value.toString());

                let successful = false;

                for (let attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                    try {
                        console.log(
                            `Attempt ${attempt}/${MAX_RETRIES} for ${post.postId}`
                        );

                        await processPost(post);

                        successful = true;
                        break;

                    } catch (error) {
                        console.log(`Attempt ${attempt} failed: ${error.message}`);

                        if (attempt < MAX_RETRIES) {
                            console.log("Retrying in 1 second...\n");

                            await new Promise(resolve =>
                                setTimeout(resolve, RETRY_DELAY)
                            );
                        }
                    }
                }

                if (successful) {
                    console.log(`Event ${post.eventId} processed successfully.`);
                } else {
                    console.log("All retry attempts failed.");
                    console.log("Message will be sent to DLQ in the next stage.");
                }

                // Commit only after successful processing.
                // Permanently failed messages are handled in the DLQ stage.
                if (successful) {
                    await consumer.commitOffsets([{
                        topic,
                        partition,
                        offset: (BigInt(message.offset) + 1n).toString(),
                    }]);
                } else {
                    throw new Error(
                        `Event ${post.eventId} failed all retry attempts`
                    );
                }
            },
        });

    } catch (error) {
        console.error("Consumer error:", error);
    }
}

consumePosts();
