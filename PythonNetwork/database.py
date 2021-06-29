import mysql.connector
from datetime import datetime, timedelta

def connectToDatabase():
    mydb = mysql.connector.connect(user='root', password='andreihoria1',
                                   host='127.0.0.1', database='sensordata',
                                   auth_plugin='mysql_native_password')
    if (mydb):
        return mydb
    else:
        print("Connection error")
        return None


def getRuntimeData(mydb):
    myCursor = mydb.cursor()
    myCursor.execute("Select * from gathereddata")
    sensorData = myCursor.fetchall()
    return sensorData


def deleteRuntimeData(mydb):
    myCursor = mydb.cursor()
    myCursor.execute("Delete from gathereddata")
    mydb.commit()


def getTrainData(mydb):
    myCursor = mydb.cursor()
    myCursor.execute("Select * from traindata")
    sensorData = myCursor.fetchall()
    # for row in sensorData:
    #     print(row)
    return sensorData


def getFeedbackData(mydb, type):
    myCursor = mydb.cursor()
    myCursor.execute("Select * from feedback where type = '" + type + "'")
    data = myCursor.fetchall()
    date = []
    reps = []
    weight = []
    for row in data:
        date.append(row[2])
        reps.append(row[3])
        weight.append(row[4])
    return date, reps, weight


def getDietData(mydb, date):
    date = date[4:]
    myCursor = mydb.cursor()
    myCursor.execute("Select * from diet where date = '" + date + "'")
    data = myCursor.fetchall()
    foodName = []
    quantity = []
    calories = []
    proteins = []
    fats = []
    for row in data:
        foodName.append(row[1])
        quantity.append(row[2])
        calories.append(row[4])
        proteins.append(row[5])
        fats.append(row[6])
    return foodName, quantity, calories, proteins, fats


def getWorkoutData(mydb, date):
    date = date.replace("-", " ")
    if date.find("June"):
        date = date.replace("June", "Jun")
    myCursor = mydb.cursor()
    myCursor.execute("Select * from workoutinfo where date = '" + date + "'")
    data = myCursor.fetchall()
    type = []
    reps = []
    weight = []
    time = []
    for row in data:
        type.append(row[1])
        reps.append(row[3])
        weight.append(row[4])
        time.append(row[5])
    return type, reps, weight, time


def addFeedbackData(mydb, reps, type, weight, date):
    myCursor = mydb.cursor()
    myCursor.execute("Select weight from feedback where date = '" + date + "' AND type = '" + type + "'")
    maxWeightList = myCursor.fetchall()
    maxWeight = 0
    if not maxWeightList:
        maxWeight = 0
    else:
        maxWeight = maxWeightList[0][0]
    if weight >= maxWeight:
        myCursor.execute("Delete from feedback where date = '" + date + "' AND type = '" + type + "'")
        mydb.commit()
        sqlString = "Insert into feedback(type, date, reps, weight) Values('" + type + "', '" + date + "', " + str(reps) + ", " + str(weight) + ")"
        myCursor.execute(sqlString)
        mydb.commit()


def addWorkoutData(mydb, reps, type, weight, date, time):
    myCursor = mydb.cursor()
    seconds = int(time/1000.0)%60
    sqlString = "Insert into workoutinfo(type, date, reps, weight, time) Values('" + type + "', '" + date + "', " + str(reps) + ", " + str(weight) + "," + str(seconds) + ")"
    myCursor.execute(sqlString)
    mydb.commit()


def getTestExData(data):
    accX = []
    accY = []
    accZ = []
    millis = []
    type = ""
    weight = 0
    timestamp = 0
    lastTs = 0
    time = 0
    it = 0
    date = ""
    for row in data:
        if it == 0:
            type = row[6]
            weight = row[7]
            timestamp = row[4]
            dateVar = datetime(1970, 1, 1) + timedelta(seconds=timestamp/1000)
            date = dateVar.strftime("%d %b %Y")
            millis.append(10)
            it = it + 1
        else:
            lastTs = row[4]
            ms = int(row[4] - timestamp)
            millis.append(ms)
            it = it + 1
        accX.append(row[1])
        accY.append(row[2])
        accZ.append(row[3])
        time = lastTs - timestamp
    return accX, accY, accZ, millis, type, weight, date, time


def getSpecificExData(data, typeP):
    accX = []
    accY = []
    accZ = []
    type = typeP
    millis = []
    timestamp = 0
    it = 0
    for row in data:
        if row[6] == typeP:
            if it == 0:
                timestamp = row[4]
                millis.append(10)
                it = it + 1
            else:
                ms = int(row[4] - timestamp)
                millis.append(ms)
                it = it + 1
            accX.append(row[1])
            accY.append(row[2])
            accZ.append(row[3])
    return accX, accY, accZ, millis, type


def getSpecificExDataArray(data, typeP):
    accX = []
    accY = []
    accZ = []
    type = typeP
    millis = []
    timestamp = 0
    it = 0
    for row in data:
        if row[6] == typeP:
            if it == 0:
                it = it + 1
                timestamp = row[4]
                millis.append((it-1, 10))
            else:
                time = int(row[4] - timestamp)
                passed = int(time - millis[len(millis)-1][1])
                if passed < 1000:
                    millis.append((it-1, time))
                else:
                    it = it + 1
                    timestamp = row[4]
                    millis.append((it-1, 10))
            accX.append((it-1, row[1]))
            accY.append((it-1, row[2]))
            accZ.append((it-1, row[3]))
    aux = 0
    accXAux = []
    accYAux = []
    accZAux = []
    millisAux = []
    accXRez = []
    accYRez = []
    accZRez = []
    millisRez = []
    for leng in range(len(accX)):
        if (accX[leng][0] != aux) | (leng == len(accX)-1):
            accXRez.append((aux, accXAux))
            accYRez.append((aux, accYAux))
            accZRez.append((aux, accZAux))
            millisRez.append((aux, millisAux))
            accXAux = []
            accYAux = []
            accZAux = []
            millisAux = []
            aux = aux+1
        accXAux.append(accX[leng][1])
        accYAux.append(accY[leng][1])
        accZAux.append(accZ[leng][1])
        millisAux.append(millis[leng][1])
    return accXRez, accYRez, accZRez, millisRez, type


def getTrainExByDesc(data, typeP, descId):
    accX = []
    accY = []
    accZ = []
    type = typeP
    millis = []
    timestamp = 0
    it = 0
    for row in data:
        if row[6] == typeP:
            if row[7] == descId:
                if it == 0:
                    timestamp = row[4]
                    millis.append(10)
                    it = it + 1
                else:
                    ms = int(row[4] - timestamp)
                    millis.append(ms)
                    it = it + 1
                accX.append(row[1])
                accY.append(row[2])
                accZ.append(row[3])
    return accX, accY, accZ, millis, type


def getTrainExByDescArray(data, typeP, descId):
    accX = []
    accY = []
    accZ = []
    type = typeP
    millis = []
    timestamp = 0
    it = 0
    for row in data:
        if row[6] == typeP:
            if row[7] == descId:
                if it == 0:
                    it = it + 1
                    timestamp = row[4]
                    millis.append((it - 1, 10))
                else:
                    time = int(row[4] - timestamp)
                    passed = int(time - millis[len(millis) - 1][1])
                    if passed < 1000:
                        millis.append((it - 1, time))
                    else:
                        it = it + 1
                        timestamp = row[4]
                        millis.append((it - 1, 10))
                accX.append((it - 1, row[1]))
                accY.append((it - 1, row[2]))
                accZ.append((it - 1, row[3]))
    aux = 0
    accXAux = []
    accYAux = []
    accZAux = []
    millisAux = []
    accXRez = []
    accYRez = []
    accZRez = []
    millisRez = []
    for leng in range(len(accX)):
        if (accX[leng][0] != aux) | (leng == len(accX)-1):
            accXRez.append((aux, accXAux))
            accYRez.append((aux, accYAux))
            accZRez.append((aux, accZAux))
            millisRez.append((aux, millisAux))
            accXAux = []
            accYAux = []
            accZAux = []
            millisAux = []
            aux = aux+1
        accXAux.append(accX[leng][1])
        accYAux.append(accY[leng][1])
        accZAux.append(accZ[leng][1])
        millisAux.append(millis[leng][1])
    return accXRez, accYRez, accZRez, millisRez, type