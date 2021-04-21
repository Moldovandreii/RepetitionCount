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
