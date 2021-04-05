using Microsoft.Extensions.Hosting;
//using MySqlConnector;
using Nancy.Json;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using System;
using System.Text;
using MySql.Data.MySqlClient;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;

namespace SensorDataMediator
{
    class Program
    {

        static void Main(string[] args)
        {
            var data = new SensorDataDTO();
            data.acc_x = 1;
            data.acc_y = 1;
            data.acc_z = 1;
            data.timestamp = 100;

            /*var connectionString = "server=localhost; user id=root; password=andreihoria1; database=sensordata";
            var con = new MySqlConnection(connectionString);
            con.Open();*/

            var factory = new ConnectionFactory
            {
                Uri = new Uri("amqps://iwzqqjvh:GHp-WHrSsYuFBbkUQwk2Suehf5uqUsTz@hawk.rmq.cloudamqp.com/iwzqqjvh")
            };
            using var connection = factory.CreateConnection();
            using var channel = connection.CreateModel();
            channel.QueueDeclare("andreiQueue", durable: true, exclusive: false, autoDelete: false, arguments: null);
            var consumer = new EventingBasicConsumer(channel);
            consumer.Received += (sender, e) =>
            {
                var body = e.Body.ToArray();
                var message = Encoding.UTF8.GetString(body);
                JavaScriptSerializer js = new JavaScriptSerializer();
                SensorDataDTO data = js.Deserialize<SensorDataDTO>(message);
                var date = (new DateTime(1970, 1, 1)).AddMilliseconds(data.timestamp).ToLocalTime().ToString();
                Console.WriteLine(data.timestamp);

                var connectionString = "server=localhost; user id=root; password=andreihoria1; database=sensordata";
                var con = new MySqlConnection(connectionString);
                con.Open();
                string query = "insert into sensordata.gathereddata(acc_x,acc_y,acc_z,timestamp,date) values('" + data.acc_x + "','" + data.acc_y + "','" + data.acc_z + "','" + data.timestamp + "','" + date + "');";
                MySqlCommand command = new MySqlCommand(query, con);
                MySqlDataReader reader = command.ExecuteReader();
                con.Close();
            };
            channel.BasicConsume("andreiQueue", true, consumer);
            while (true)
            {

            }
        }
    }
}
