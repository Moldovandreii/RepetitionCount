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
data = db.getTestData(myDb)
# data = db.getTrainData(myDb)

# accXEx, accYEx, accZEx, millisEx, typeEx = db.getSpecificExData(data, "Bench Press")
# accXEx, accYEx, accZEx, millisEx, typeEx = db.getSpecificExDataTest(data, "Bench Press")
# charts.plotData(accXEx, accYEx, accZEx, millisEx, typeEx)
# plt.show()


reps = signalProcessing.findPeaksBenchPress(data)
print "Number of reps =", reps
rb.publishRabbitResult(str(reps))
















