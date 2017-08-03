package com.lucent.app.gui;

/**
 * Created by Lucent on 7/29/2017.
 */

import com.lucent.app.NameRepo;
import com.lucent.app.LibraryUtil;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

class StudentTimeTableModel extends AbstractTableModel {
    private NameRepo repo;
    private final static String[] columnNames = {"ID",
            "# of Hours",
    };//same as before...

    public StudentTimeTableModel(){
        repo = NameRepo.getInstance();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return repo.getStudents().size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int r, int c) {
        List<String> students = new ArrayList<>(repo.getStudents());
        if(c == 0){
            return students.get(r);
        } else{
            LibraryUtil.HourMinute hm = LibraryUtil.convertToHour( repo.searchName(students.get(r)));
            return hm.hours + " hours, " + hm.minutes + " minutes";
        }
    }

    public int getRowByValue(String name) {
        for (int i = this.getRowCount() - 1; i >= 0; --i) {
            for (int j = this.getColumnCount() - 1; j >= 0; --j) {
                if (this.getValueAt(i, j).equals(name)) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * Adds name to table and repo
     * @param name
     */
    public void addStudent(String name){
        repo.addName(name);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    /**
     * removes the student from the table and repo
     * @param name
     */
    public void removeStudent(String name){
        repo.removeName(name);
        fireTableRowsDeleted(getRowByValue(name) - 1,getRowByValue(name) - 1);
    }

}