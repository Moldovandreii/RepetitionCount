import pika
import os


def publishRabbitResult(repCount):
    url = os.environ.get('CLOUDAMQP_URL', 'amqps://iwzqqjvh:GHp-WHrSsYuFBbkUQwk2Suehf5uqUsTz@hawk.rmq.cloudamqp.com/iwzqqjvh')
    params = pika.URLParameters(url)
    connection = pika.BlockingConnection(params)
    channel = connection.channel()  # start a channel
    channel.queue_declare(queue='repCountResult')  # Declare a queue
    channel.basic_publish(exchange='',
                          routing_key='repCountResult',
                          body=repCount)
    connection.close()


def finishSendingData():
    url = os.environ.get('CLOUDAMQP_URL',
                         'amqps://iwzqqjvh:GHp-WHrSsYuFBbkUQwk2Suehf5uqUsTz@hawk.rmq.cloudamqp.com/iwzqqjvh')
    params = pika.URLParameters(url)
    connection = pika.BlockingConnection(params)
    channel = connection.channel()
    channel.queue_declare(queue='finishSending', durable=True)

    result = ""

    def callback(ch, method, properties, body):
        message = str(body)
        result = message
        print(" [x] Received " + message)
        channel.stop_consuming()

    channel.basic_consume('finishSending', callback, auto_ack=True)
    channel.start_consuming()
    connection.close()
    return result
