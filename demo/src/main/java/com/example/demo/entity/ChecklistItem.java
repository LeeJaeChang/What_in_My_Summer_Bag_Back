package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "checklist_items")
public class ChecklistItem {

    @Id
    @Column(name = "checklist_item_id")
    private String checklistItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_essential", nullable = false)
    private boolean isEssential;

    @Column(nullable = false)
    private boolean checked = false;

    @Column
    private String reason;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    protected ChecklistItem() {
    }

    public ChecklistItem(String checklistItemId, Checklist checklist, String name, boolean isEssential,
                         boolean checked, String reason, int sortOrder) {
        this.checklistItemId = checklistItemId;
        this.checklist = checklist;
        this.name = name;
        this.isEssential = isEssential;
        this.checked = checked;
        this.reason = reason;
        this.sortOrder = sortOrder;
    }

    public String getChecklistItemId() {
        return checklistItemId;
    }

    public Checklist getChecklist() {
        return checklist;
    }

    public String getName() {
        return name;
    }

    public boolean isEssential() {
        return isEssential;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getReason() {
        return reason;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
