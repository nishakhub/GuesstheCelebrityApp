package com.rajramchandani.guessthecelebrityapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> urls=new ArrayList<String>();
    ArrayList<String> names=new ArrayList<String>();
    ArrayList<String> rands=new ArrayList<String>();
    Bitmap myimage;
    ImageView imageView;
    int correctlocation=0;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    int a=0;
    int b=0;
    Random random=new Random();

    public void click(View view) throws ExecutionException, InterruptedException {
        if(view.getTag().toString().equals(Integer.toString(correctlocation)))
        {
            Toast.makeText(getApplicationContext(),"Correct",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"InCorrect",Toast.LENGTH_SHORT).show();
        }

        rands.clear();
        finding();
    }

    public void finding() {
        a=random.nextInt(urls.size());

        DownloadImage image=new DownloadImage();
        try {
            myimage=image.execute(urls.get(a)).get();
            imageView.setImageBitmap(myimage);

            correctlocation=random.nextInt(4);

            for(int i=0;i<4;i++)
            {
                b=random.nextInt(names.size());
                while(rands.contains(names.get(b)) && b!=a) {
                    b=random.nextInt(names.size());
                }
                rands.add(names.get(b));
            }

            rands.set(correctlocation,names.get(a));

            button0.setText(rands.get(0));
            button1.setText(rands.get(1));
            button2.setText(rands.get(2));
            button3.setText(rands.get(3));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
    public class DownloadImage extends AsyncTask<String,Void,Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {

            URL url;
            HttpURLConnection urlConnection=null;
            try {

                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream inputStream=urlConnection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

     public class Download extends AsyncTask<String ,Void,String>
    {

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                String result="";
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream inputStream=urlConnection.getInputStream();
                InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
                int data=inputStream.read();
                while(data!=-1)
                {
                   char current=(char)data;
                   result+=current;
                   data=inputStream.read();
                }

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return "failed";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button0=(Button)findViewById(R.id.button0);
        button1=(Button)findViewById(R.id.button1);
        button2=(Button)findViewById(R.id.button2);
        button3=(Button)findViewById(R.id.button3);
        imageView=(ImageView)findViewById(R.id.imageView2);
        String result1="";
        String resultraj="";

        Download download=new Download();
        try {
            result1=download.execute("http://www.imdb.com/list/ls064256289/").get();
           // resultraj
            //
            // = download.execute("http://nisharaj.000webhostapp.com/index.html").get();
            String resultsplit[]=result1.split("<div class=\"header filmosearch\">");
            result1=resultsplit[1];
            String splitresult[]=result1.split("<div class=\"footer filmosearch\">");
            result1=splitresult[0];
            Pattern p=Pattern.compile("src=\"(.*?)\"");

            Matcher m=p.matcher(result1);
            //Matcher mraj=p.matcher(resultraj);
            while(m.find())
            {
                urls.add(m.group(1));
            }
            /*while(mraj.find())
            {
                urls.add(mraj.group(1));
            }*/
            Pattern p1=Pattern.compile("alt=\"(.*?)\"");
            Matcher m1=p1.matcher(result1);
            //Matcher mnisha=p1.matcher(resultraj);
            while(m1.find())
            {
                names.add(m1.group(1));
            }
            /*while(mnisha.find())
            {
                names.add(mnisha.group(1));
            }*/

            finding();






        } catch (InterruptedException e) {
            e.printStackTrace();

        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}
