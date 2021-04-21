import matplotlib.pyplot as plt
import matplotlib.patches as mpatches


def plotAccData(accX, accY, accZ, millis, peak_pos, height, min_pos, real_min_pos):
    figure = plt.figure()
    axes = figure.add_subplot(1, 1, 1)
    plt.ylabel("Accelerometer Data(m/s^2)")
    plt.xlabel("Time(ms)")
    axes.plot(millis, accX, label='X Axis')
    axes.plot(millis, accY, label='Y Axis')
    axes.plot(millis, accZ, label='Z Axis')
    axes.scatter(peak_pos, height, color='r', s=15, marker='D', label='Maxima')
    axes.scatter(min_pos, real_min_pos, color='gold', s=15, marker='X', label='Minima')
    axes.legend()
    axes.grid()
    return figure


def plotData(accX, accY, accZ, millis, type):
    figure = plt.figure()
    axes = figure.add_subplot(1, 1, 1)
    plt.title(type)
    plt.ylabel("Accelerometer Data(m/s^2)")
    plt.xlabel("Time(ms)")
    axes.plot(accX, label='X Axis')
    axes.plot(accY, label='Y Axis')
    axes.plot(accZ, label='Z Axis')
    axes.legend()
    return figure
