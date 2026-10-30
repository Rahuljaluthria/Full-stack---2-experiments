
const kafka = require("../config/kafka");
const { randomUUID } = require("crypto");

const producer = kafka.producer();

async function publishPost() {
    try {
        await producer.connect();

        console.log("Producer connected to Kafka");

        const post = {
    eventId: randomUUID(),
    postId: "POST102",
    content: "Testing Kafka retry mechanism",
    scheduledAt: new Date().toISOString(),
    status: "SCHEDULED",
    simulateFailure: true,
    };
        await producer.send({
            topic: "scheduled-posts",
            messages: [
                {
                    key: post.postId,
                    value: JSON.stringify(post),
                },
            ],
        });

        console.log("Post event sent successfully:");
        console.log(post);

    } catch (error) {
        console.error("Producer error:", error);
    } finally {
        await producer.disconnect();
    }
}

publishPost();
