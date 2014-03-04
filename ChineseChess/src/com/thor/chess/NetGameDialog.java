package com.thor.chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;

import android.bluetooth.*;

class NetAdapter extends BaseAdapter
{
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, String>> data;

    public NetAdapter(Context context, ArrayList<HashMap<String, String>> data)
    {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public int getCount()
    {
        return data.size();
    }

    public Object getItem(int arg0)
    {
        return null;
    }

    public long getItemId(int arg0)
    {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (parent.getTag() == null || (Integer) parent.getTag() != position)
        {
            // No select any item
            if (convertView == null || convertView.getTag() != null)
            {
                convertView = inflater.inflate(R.layout.list_item_simple, null);
            }
            ((TextView) convertView).setText(data.get(position).get("title"));
        }
        else
        {
            if (convertView == null || convertView.getTag() == null)
            {
                convertView = inflater.inflate(R.layout.list_item, null);
            }
            Integer ival = Integer.valueOf(1);
            convertView.setTag(ival);
            TextView title = (TextView) convertView.findViewById(R.id.txt_host_title);
            title.setText(data.get(position).get("title"));
            TextView ip = (TextView) convertView.findViewById(R.id.txt_host_ip);
            ip.setText(data.get(position).get("ip"));
            TextView gameInfo = (TextView) convertView.findViewById(R.id.txt_game_info);
            gameInfo.setText(data.get(position).get("info"));
        }
        return convertView;
    }
}


class ServerInfo
{
    public String title;
    public String ip;
    public String description;
    public boolean isClose;
    private final static Pattern serverAdd = Pattern.compile("^s:(.+)\t(.+)\n$");
    private final static Pattern serverRemove = Pattern.compile("^s:\n$");

    public static ServerInfo parse(DatagramPacket packet)
    {
        if (packet.getLength() > 0)
        {
            try
            {
                String cmd = new String(packet.getData(), "UTF-8");
                Matcher matcher = serverAdd.matcher(cmd);
                if (matcher.find())
                {
                    ServerInfo serverInfo = new ServerInfo();
                    serverInfo.title = matcher.group(1);
                    serverInfo.description = matcher.group(2);
                    serverInfo.ip = packet.getAddress().getHostAddress();
                    serverInfo.isClose = false;
                    return serverInfo;
                }
                else
                {
                    matcher = serverRemove.matcher(cmd);
                    if (matcher.find())
                    {
                        ServerInfo serverInfo = new ServerInfo();
                        serverInfo.isClose = true;
                        serverInfo.ip = packet.getAddress().getHostAddress();
                        return serverInfo;
                    }
                }
            }
            catch (Exception e)
            {
                return null;
            }
        }
        return null;
    }
}

public class NetGameDialog extends Dialog
{
    private Context context;
    private String playerName;
    private GameListener listener = null;
    private NetAdapter adapter = null;
    private ArrayList<HashMap<String, String>> data =
            new ArrayList<HashMap<String, String>>();
    private ListView listView = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private ProgressDialog progressDialog = null;


    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                    String address = device.getAddress();
                    String name = addressToName(address, device.getName());
                    if (name.trim().length() == 0)
                    {
                        name = address;
                    }
                    data.add(new ListItem(name, address, "tot"));
                    adapter.notifyDataSetChanged();
                }
                // When discovery is finished, change the Activity title
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                closeProgressBar();
            }
        }
    };

    private void closeProgressBar()
    {
        progressDialog.cancel();
    }

    private void showProgressBar()
    {
        progressDialog = ProgressDialog.show(
                context, null, "tot....", true, true, new OnCancelListener()
        {
            public void onCancel(DialogInterface dialog)
            {
                if (bluetoothAdapter.isDiscovering())
                {
                    bluetoothAdapter.cancelDiscovery();
                }
            }
        });
    }

    public NetGameDialog(Context context)
    {
        super(context);
        this.context = context;
    }

    public void setListener(GameListener listener)
    {
        this.listener = listener;
    }

    class ListItem extends HashMap<String, String>
    {
        private static final long serialVersionUID = -891461887415985079L;

        public ListItem(String title, String ip, String info)
        {
            put("title", title);
            put("ip", ip);
            put("info", info);
        }
    }

    private String getSelectAddress()
    {
        if (listView.getTag() == null)
        {
            return null;
        }
        int pos = (Integer) listView.getTag();
        if (pos >= 0 && pos < data.size())
        {
            return data.get(pos).get("ip");
        }
        else
        {
            return null;
        }
    }

    public void startDiscovery()
    {
        if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON)
        {
            Set<BluetoothDevice> pairedDevices =
                    bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices)
            {
                String address = device.getAddress();
                String name = addressToName(address, device.getName());
                if (name.trim().length() == 0)
                {
                    name = address;
                }
                data.add(new ListItem(name, address, "��Ե�"));
            }
            showProgressBar();
            bluetoothAdapter.startDiscovery();
        }
    }

    public void startNetServer()
    {
        final NetEngine engine = new NetEngine(playerName, 0);
        dismiss();
        if (listener != null)
        {
            listener.onCreateEngine(engine, true);
        }
    }

    private static String addressToName(String address, String defaultName)
    {
        if (defaultName == null)
        {
            defaultName = "";
        }
        return ChessApplication.getSetting(address, defaultName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_menu);
        setTitle(R.string.net_game);
        EditText username = (EditText) findViewById(R.id.txt_player_name);
        listView = (ListView) findViewById(R.id.list_games);
        username.setText(ChessApplication.getSetting("Player", ""));

        adapter = new NetAdapter(context, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3)
            {
                listView.setTag(position);
            }
        });

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(receiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices)
        {
            String address = device.getAddress();
            String name = addressToName(address, device.getName());
            if (name.trim().length() == 0)
            {
                name = address;
            }
            data.add(new ListItem(name, address, "��Ե�"));
        }

        ////////////////////////////////////////////////////////////
        // Setup button events
        Button btnDiscovery = (Button) findViewById(R.id.btn_discovery);
        btnDiscovery.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                data.clear();
                adapter.notifyDataSetChanged();
                if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON)
                {
                    Set<BluetoothDevice> pairedDevices =
                            bluetoothAdapter.getBondedDevices();
                    for (BluetoothDevice device : pairedDevices)
                    {
                        data.add(new ListItem(device.getName(),
                                device.getAddress(), "��Ե�"));
                    }
                    showProgressBar();
                    bluetoothAdapter.startDiscovery();
                }
                else
                {
                    ensureBluetoothOn();
                }
            }
        });

        final Button btnJoin = (Button) findViewById(R.id.btn_join_host);
        btnJoin.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (!verifyName())
                {
                    return;
                }
                ChessApplication.setSetting("Player", playerName);
                String address = getSelectAddress();
                if (address == null)
                {
                    Toast toast = Toast.makeText(context, "��ѡ��һ���豸",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                final ProgressDialog dialog = ProgressDialog.show(
                        context, null, "���ӵ��豸...", true, false);
                final NetEngine engine = new NetEngine(playerName, address);
                engine.connect(new IConnectListener()
                {
                    public void onCompleted(boolean connectionEstablished)
                    {
                        if (connectionEstablished)
                        {
                            btnJoin.post(new Runnable()
                            {
                                public void run()
                                {
                                    dialog.cancel();
                                    dismiss();
                                    if (listener != null)
                                    {
                                        listener.onCreateEngine(engine, false);
                                    }
                                }
                            });
                        }
                        else
                        {
                            btnJoin.post(new Runnable()
                            {
                                public void run()
                                {
                                    dialog.cancel();
                                    Toast toast = Toast.makeText(context,
                                            "�޷����ӵ��豸", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
                    }
                });
            }
        });

        Button btnNew = (Button) findViewById(R.id.btn_new_host);
        btnNew.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (!verifyName())
                {
                    return;
                }
                ChessApplication.setSetting("Player", playerName);

                if (bluetoothAdapter.getScanMode() ==
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
                {

                    startNetServer();

                }
                else
                {
                    ensureDiscoverable();
                }
            }
        });


        setOnDismissListener(new OnDismissListener()
        {
            public void onDismiss(DialogInterface dialog)
            {
                if (bluetoothAdapter.isDiscovering())
                {
                    bluetoothAdapter.cancelDiscovery();
                }
                context.unregisterReceiver(receiver);
            }
        });

    }

    private void ensureBluetoothOn()
    {
        if (!bluetoothAdapter.isEnabled())
        {
            Intent enableIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) context).startActivityForResult(enableIntent, 1);
        }
    }

    private void ensureDiscoverable()
    {
        if (bluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
        {
            Intent discoverableIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            ((Activity) context).startActivityForResult(discoverableIntent, 2);
        }
    }

    private boolean verifyName()
    {
        EditText username = (EditText) findViewById(R.id.txt_player_name);
        playerName = username.getText().toString().trim();
        byte[] nameBytes = playerName.getBytes();
        if (playerName.length() == 0 || nameBytes.length > 24)
        {
            Toast toast = Toast.makeText(context, "�������ǳ�,�𳬹�8������",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, -100);
            toast.show();
            return false;
        }
        else
        {
            return true;
        }
    }
}
