
const processedEvents = new Set();

function isAlreadyProcessed(eventId) {
    return processedEvents.has(eventId);
}

function markAsProcessed(eventId) {
    processedEvents.add(eventId);
}

module.exports = {
    isAlreadyProcessed,
    markAsProcessed,
};
