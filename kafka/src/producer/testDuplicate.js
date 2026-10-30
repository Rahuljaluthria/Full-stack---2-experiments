const kafka = require("../config/kafka");

const producer = kafka.producer();

async function sendDuplicateEvents() {
    await producer.connect();

    const event = {
        eventId: "DUPLICATE-101",
        postId: "POST-DUPLICATE",
        content: "Testing idempotency",
        simulateFailure: false
    };

    await producer.send({
        topic: "scheduled-posts",
        messages: [
            {
                key: event.postId,
                value: JSON.stringify(event)
            },
            {
                key: event.postId,
                value: JSON.stringify(event)
            }
        ]
    });

    console.log("Two identical events sent!");

    await producer.disconnect();
}

sendDuplicateEvents();