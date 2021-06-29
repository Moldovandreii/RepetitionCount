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
using SensorDataMediator.DTO;

namespace SensorDataMediator
{
    class Program
    {

        static void Main(string[] args)
        {
            //var data = new SensorDataDTO();
            //data.acc_x = 1;
            //data.acc_y = 1;
            //data.acc_z = 1;
            //data.timestamp = 100;

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
                var connectionString = "server=localhost; user id=root; password=andreihoria1; database=sensordata";
                var con = new MySqlConnection(connectionString);
                con.Open();
                if (message.Contains("quantity"))
                {
                    DietDataDTO diet = js.Deserialize<DietDataDTO>(message);
                    var query = "Select calories, proteins, fat from sensordata.food where foodName = '" + diet.foodName + "'";
                    MySqlCommand command = new MySqlCommand(query, con);
                    MySqlDataReader reader = command.ExecuteReader();
                    var calories = 0;
                    var proteins = 0;
                    var fats = 0;
                    while (reader.Read())
                    {
                        calories = Int32.Parse(reader.GetString(0));
                        proteins = Int32.Parse(reader.GetString(1));
                        fats = Int32.Parse(reader.GetString(2));
                        Console.WriteLine(diet.date);
                    }
                    double actualCalories = (diet.quantity * calories) / 100.0;
                    double actualProteins = (diet.quantity * proteins) / 100.0;
                    double actualFats = (diet.quantity * fats) / 100.0;
                    reader.Close();
                    query = "Insert into sensordata.diet(foodName,quantity,date,calories,proteins,fat) values('" + diet.foodName + "','" + diet.quantity + "','" + diet.date + "','" + actualCalories + "','" + actualProteins + "','" + actualFats + "');";
                    command = new MySqlCommand(query, con);
                    command.ExecuteReader();
                }
                else
                {
                    SensorDataDTO data = js.Deserialize<SensorDataDTO>(message);
                    //SensorDataTrainDTO data = js.Deserialize<SensorDataTrainDTO>(message);
                    var date = (new DateTime(1970, 1, 1)).AddMilliseconds(data.timestamp).ToLocalTime().ToString();
                    Console.WriteLine(data.timestamp);
                    var query = "insert into sensordata.gathereddata(acc_x,acc_y,acc_z,timestamp,date,type, weight) values('" + data.acc_x + "','" + data.acc_y + "','" + data.acc_z + "','" + data.timestamp + "','" + date + "','" + data.type + "','" + data.weight + "');";
                    //var query = "insert into sensordata.traindata(acc_x,acc_y,acc_z,timestamp,date,type,descriptionId) values('" + data.acc_x + "','" + data.acc_y + "','" + data.acc_z + "','" + data.timestamp + "','" + date + "','" + data.type + "','" + data.descriptionId + "');";
                    MySqlCommand command = new MySqlCommand(query, con);
                    MySqlDataReader reader = command.ExecuteReader();
                }
                con.Close();
            };
            channel.BasicConsume("andreiQueue", true, consumer);
            while (true)
            {

            }
        }
    }
}
