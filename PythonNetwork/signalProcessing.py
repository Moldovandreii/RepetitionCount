import numpy as np
from scipy.signal import find_peaks
import matplotlib.pyplot as plt
import charts
import database as db


def findPeaksBenchPress(data):
    accXData, accYData, accZData, millis = db.getTestExData(data)
    signal = accZData
    y = np.array(signal)
    x = np.array(millis)
    # find peaks
    peaks = find_peaks(y, height=(-5, 3))
    height = peaks[1]['peak_heights']   # list of the heights of the peaks
    peak_pos = x[peaks[0]]              # list of hte peak positions
    # find minima
    y2 = y*-1
    minima = find_peaks(y2, height=2)
    min_pos = x[minima[0]]              # list of the minima positions
    min_height = y2[minima[0]]          # list of the mirrored minima heights
    real_min_height = min_height*-1
    # plot data
    charts.plotAccData(accXData, accYData, accZData, millis, peak_pos, height, min_pos, real_min_height)
    plt.show()
    message = len(peak_pos)
    return message

