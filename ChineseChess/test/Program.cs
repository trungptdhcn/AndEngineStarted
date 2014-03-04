using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;

namespace test
{
    class Program
    {

        class StateObject
        {
            public byte[] buffer = new byte[1024];
            public Socket socket = null;
        }

        static void OnReceive(IAsyncResult result)
        {
            if (result.IsCompleted)
            {
                StateObject stateObj = (StateObject)result.AsyncState;
                try
                {
                    EndPoint sender = new IPEndPoint(IPAddress.Any, 0);
                    int receivedBytes = stateObj.socket.EndReceiveFrom(result, ref sender);
                    if (receivedBytes > 0)
                    {
                        System.Console.WriteLine(Encoding.UTF8.GetString(stateObj.buffer, 0, receivedBytes));
                    }
                    sender = new IPEndPoint(IPAddress.Any, 0);
                    stateObj.socket.BeginReceiveFrom(stateObj.buffer, 0, stateObj.buffer.Length,
                        SocketFlags.None, ref sender, new AsyncCallback(OnReceive), stateObj);
                }
                catch (Exception)
                {
                    System.Console.WriteLine("套接字已关闭。");
                }
            }
        }

        static void Main(string[] args)
        {
            StateObject stateObj = new StateObject();
            stateObj.socket = new Socket(AddressFamily.InterNetwork, 
                SocketType.Dgram, ProtocolType.Udp);
            stateObj.socket.SetSocketOption(SocketOptionLevel.Socket, 
                SocketOptionName.Broadcast, 1);
            IPEndPoint ep = new IPEndPoint(IPAddress.Any, 9876);
            stateObj.socket.Bind(ep);
            EndPoint sender = new IPEndPoint(IPAddress.Any, 0);

            stateObj.socket.BeginReceiveFrom(stateObj.buffer, 0, stateObj.buffer.Length,
                SocketFlags.None, ref sender, new AsyncCallback(OnReceive), stateObj);


            while (true)
            {
                string cmd = Console.ReadLine();
                if (cmd == "s")
                {
                    IPEndPoint destEP = new IPEndPoint(IPAddress.Parse("10.202.97.255"), 9876);
                    try
                    {
                        stateObj.socket.SendTo(Encoding.UTF8.GetBytes("s:test\tdesc\n"), destEP);
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine(e.Message);
                    }
                }
                else if (cmd == "q")
                    break;
                else if (cmd == "c")
                {
                    try
                    {
                        stateObj.socket.Shutdown(SocketShutdown.Both);
                        stateObj.socket.Close();
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine(e.Message);
                    }
                }
            }
        }
    }
}
