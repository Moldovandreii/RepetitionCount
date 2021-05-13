using System;
using System.Collections.Generic;
using System.Text;

namespace SensorDataMediator.DTO
{
    class SensorDataTrainDTO
    {
        public float acc_x { get; set; }
        public float acc_y { get; set; }
        public float acc_z { get; set; }
        public long timestamp { get; set; }
        public string type { get; set; }
        public int descriptionId { get; set; }
    }
}
