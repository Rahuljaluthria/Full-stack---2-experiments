
const { Kafka } = require("kafkajs");

const kafka = new Kafka({
    clientId: "scheduled-post-system",
    brokers: ["localhost:9092"],
});

module.exports = kafka;
