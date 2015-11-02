package com.makeblaze.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makeblaze.myapplication.Services.DownloadService;
import com.makeblaze.myapplication.entities.FileInfo;

import java.util.List;

/**
 * Created by jz on 2015/10/30.
 */
public class FilelistAdapter extends BaseAdapter {


    private Context context;
    private List<FileInfo> fileList;
    private LayoutInflater layoutInflater;

    public FilelistAdapter(Context context, List<FileInfo> fileList) {
        this.context = context;
        this.fileList = fileList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final FileInfo fileInfo = fileList.get(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.sample_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.filename = (TextView) convertView.findViewById(R.id.content);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressbar);
            viewHolder.start = (Button) convertView.findViewById(R.id.start);
            viewHolder.stop = (Button) convertView.findViewById(R.id.stop);
            viewHolder.filename.setText(fileInfo.getFilename());
            viewHolder.progressBar.setMax(100);
            viewHolder.start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.setAction(DownloadService.Action_START);
                    Log.i("intent", "setAction(DownloadService.Action_START)");
                    intent.putExtra("fileInfo", fileInfo);
                    context.startService(intent);
                    Log.i("intent", "startService)");
                }
            });
            viewHolder.stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.setAction(DownloadService.Action_STOP);
                    intent.putExtra("fileInfo", fileInfo);
                    context.startService(intent);
                    Log.i("intent", "startService)");
                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.progressBar.setProgress(fileInfo.getFinfished());
        return convertView;
    }

    public void updateprogress(int id, int progress) {
        FileInfo fileInfo = fileList.get(id - 1);
        Log.i("fileInfo", fileInfo.toString());
        fileInfo.setFinfished(progress);
        notifyDataSetChanged();
    }

    class ViewHolder {
        ProgressBar progressBar;
        TextView filename;
        Button start, stop;
    }
}
