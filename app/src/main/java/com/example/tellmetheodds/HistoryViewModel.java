package com.example.tellmetheodds;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.LinkedList;

public class HistoryViewModel extends ViewModel {
    private MutableLiveData<LinkedList<RoundRecord>> roundRecord;

    public HistoryViewModel(){
        roundRecord = new MutableLiveData<>();
        addRoundRecord(new RoundRecord(0)); //add round 0 record
    }

    public LiveData<LinkedList<RoundRecord>> getRoundRecord(){
        if (roundRecord == null){
            roundRecord = new MutableLiveData<>();
        }
        return roundRecord;
    }

    public void addRoundRecord(RoundRecord record){
        if (roundRecord.getValue() == null){
            roundRecord.setValue(new LinkedList<>());
        }
        roundRecord.getValue().add(record);
        updateList();
    }

    public void removeLastRoundRecord(){
        if (roundRecord.getValue() != null){
            roundRecord.getValue().removeLast();
        }
    }

    public void updateList(){
        roundRecord.setValue(roundRecord.getValue());
    }

    public void addThrowRecord(ThrowRecord record){
        roundRecord.getValue().getLast().addThrow(record);
    }
}

