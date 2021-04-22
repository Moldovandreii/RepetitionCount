package com.example.repetitioncounting.rabbitMQ

import android.os.AsyncTask
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

class RabbitServer {
    private val connectionFact: ConnectionFactory
    private var channel: Channel? = null
    private var connection: Connection? = null

    init{
        connectionFact = ConnectionFactory()
        connectionFact.setUri("amqps://iwzqqjvh:GHp-WHrSsYuFBbkUQwk2Suehf5uqUsTz@hawk.rmq.cloudamqp.com/iwzqqjvh")
    }

    fun getFactory(): ConnectionFactory{
        return connectionFact
    }

    fun getChannel(): Channel?{
        return channel
    }

    fun getConnection(): Connection?{
        return connection
    }

    fun defaultExchangeAndQueue(): RabbitServer{
        val newConnection = getFactory().newConnection()
        val channel = newConnection.createChannel()

        channel.queueDeclare("andreiQueue", true, false, false, emptyMap())
        channel.queueDeclare("finishSending", true, false, false, emptyMap())

        val obj = RabbitServer()
        obj.channel = channel
        obj.connection = newConnection
        return obj

        /*channel.close()
        newConnection.close()*/
    }
}