# coding=utf-8
# This is a sample Python script.
# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import database as db
import matplotlib.pyplot as plt
import charts
import signalProcessing
import rabbitMq as rb

myDb = db.connectToDatabase()

# ----------------------------------------------------------------------------------------------------------------------

# data = db.getTrainData(myDb)
# reps = signalProcessing.findPeaksTrain(data, "Bench Press", 2, 1)

# ----------------------------------------------------------------------------------------------------------------------

while 1:
    result = rb.finishSendingData()          # wait for user to stop exercising
    if result == "Done sending":
        data = db.getTestData(myDb)
        db.deleteTestData(myDb)

        reps, type, weight, date = signalProcessing.findPeaks(data)
        db.addFeedbackData(myDb, reps, type, weight, date)
        print "Number of reps =", reps
        rb.publishRabbitResult(reps)
    else:
        data = db.getFeedbackData(myDb, result)
        dataString = "\n".join(map(str, data))
        rb.publishRabbitResult(dataString)
















