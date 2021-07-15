# coding=utf-8
# This is a sample Python script.
# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import database as db
import matplotlib.pyplot as plt
import charts
import signalProcessing
import rabbitMq as rb

# ----------------------------------------------------------------------------------------------------------------------

# myDb = db.connectToDatabase()
# data = db.getTrainData(myDb)
# reps = signalProcessing.findPeaksTrain(data, "Seated Rows", 0, 1)
#
# myDb.close()

# ----------------------------------------------------------------------------------------------------------------------

while 1:
    result = rb.finishSendingData()          # wait for user to stop exercising
    if result == "Done sending":
        myDb = db.connectToDatabase()
        data = db.getRuntimeData(myDb)
        db.deleteRuntimeData(myDb)

        reps, type, weight, date, time = signalProcessing.findPeaks(data)
        db.addFeedbackData(myDb, reps, type, weight, date)
        db.addWorkoutData(myDb, reps, type, weight, date, time)
        print "Number of reps =", reps
        rb.publishRabbitResult(reps)
        myDb.close()
    elif result.find('-') != -1:
        myDb = db.connectToDatabase()
        data = db.getWorkoutData(myDb, result)
        myDb.close()
        dataString = "\n".join(map(str, data))
        rb.publishRabbitResult(dataString)
    elif result.find("Diet") != -1:
        myDb = db.connectToDatabase()
        data = db.getDietData(myDb, result)
        myDb.close()
        dataString = "\n".join(map(str, data))
        rb.publishRabbitResult(dataString)
    else:
        myDb = db.connectToDatabase()
        data = db.getFeedbackData(myDb, result)
        myDb.close()
        dataString = "\n".join(map(str, data))
        rb.publishRabbitResult(dataString)
















