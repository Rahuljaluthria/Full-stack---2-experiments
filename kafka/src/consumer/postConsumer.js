
const kafka = require("../config/kafka");

const consumer = kafka.consumer({
    groupId: "scheduled-post-consumer-group",
});

async function consumePosts() {
    try {
        await consumer.connect();

        console.log("Consumer connected to Kafka");

        await consumer.subscribe({
            topic: "scheduled-posts",
            fromBeginning: true,
        });

        console.log("Waiting for scheduled posts...\n");

        await consumer.run({
            eachMessage: async ({ topic, partition, message }) => {

                const post = JSON.parse(
                    message.value.toString()
                );

                console.log("Received new post event:");
                console.log("-----------------------------");

                console.log("Topic:", topic);
                console.log("Partition:", partition);
                console.log("Event ID:", post.eventId);
                console.log("Post ID:", post.postId);
                console.log("Content:", post.content);
                console.log("Scheduled At:", post.scheduledAt);
                console.log("Status:", post.status);

                console.log("-----------------------------");
                console.log("Post processed successfully!\n");
            },
        });

    } catch (error) {
        console.error("Consumer error:", error);
    }
}

consumePosts();
