import numpy as np
from scipy.signal import find_peaks
import matplotlib.pyplot as plt
import charts
import database as db


def findPeaksBenchPress(accXData, accYData, accZData, millis):
    signal = accYData
    y = np.array(signal)
    x = np.array(millis)
    # find peaks - normal execution
    peaks = find_peaks(y, height=(-10, 12), width=3)
    # peaks = find_peaks(y, height=(-10, 10), distance=20)
    height = peaks[1]['peak_heights']
    peak_pos = x[peaks[0]]
    # find minima
    y2 = y*-1
    minima = find_peaks(y2, height=2)
    min_pos = x[minima[0]]
    min_height = y2[minima[0]]
    real_min_height = min_height*-1
    # plot data
    # charts.plotAccData(accXData, accYData, accZData, millis, peak_pos, height, min_pos, real_min_height)
    # plt.show()

    # find peaks - half rep
    peaksHR = find_peaks(y, height=(-5, 3))
    heightHR = peaksHR[1]['peak_heights']
    peak_posHR = x[peaksHR[0]]

    message = str(len(peak_pos))

    return message


def findPeaksDeadlift(accXData, accYData, accZData, millis):
    signal = accZData
    y = np.array(signal)
    x = np.array(millis)
    # find peaks
    peaks = find_peaks(y, height=(-10, 10))
    height = peaks[1]['peak_heights']  # list of the heights of the peaks
    peak_pos = x[peaks[0]]  # list of the peak positions
    # find minima
    y2 = y * -1
    minima = find_peaks(y2, height=2)
    min_pos = x[minima[0]]  # list of the minima positions
    min_height = y2[minima[0]]  # list of the mirrored minima heights
    real_min_height = min_height * -1
    # plot data
    charts.plotAccData(accXData, accYData, accZData, millis, peak_pos, height, min_pos, real_min_height)
    plt.show()
    message = str(len(peak_pos))
    return message


def findPeaks(data):
    accXEx, accYEx, accZEx, millisEx, typeEx, weight, date = db.getTestExData(data)
    reps = ""
    if typeEx == "Bench Press":
        reps = findPeaksBenchPress(accXEx, accYEx, accZEx, millisEx)
    if typeEx == "Deadlift":
        reps = findPeaksDeadlift(accXEx, accYEx, accZEx, millisEx)
    return reps, typeEx, weight, date


def findPeaksTrain(data, typeEx, descId, pos):
    accXExAr, accYExAr, accZExAr, millisExAr, typeEx = db.getTrainExByDescArray(data, typeEx, descId)
    accXEx = accXExAr[pos-1][1]
    accYEx = accYExAr[pos-1][1]
    accZEx = accZExAr[pos-1][1]
    millisEx = millisExAr[pos-1][1]
    message = ""
    if typeEx == "Bench Press":
        message = findPeaksBenchPress(accXEx, accYEx, accZEx, millisEx)
    if typeEx == "Deadlift":
        message = findPeaksDeadlift(accXEx, accYEx, accZEx, millisEx)
    if typeEx == "Skullcrushers":
        message = findPeaksDeadlift(accXEx, accYEx, accZEx, millisEx)
    return message
