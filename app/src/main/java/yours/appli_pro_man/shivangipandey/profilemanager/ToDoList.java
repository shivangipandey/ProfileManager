package yours.appli_pro_man.shivangipandey.profilemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import yours.appli_pro_man.shivangipandey.profilemanager.R;

public class ToDoList extends AppCompatActivity {

    CheckBox[] checkBoxes = new CheckBox[5];
    int[] checkvoxIDs = {R.id.checkbox_work_1,R.id.checkbox_work_2,R.id.checkbox_work_3,R.id.checkbox_work_4,R.id.checkbox_work_5};
    EditText[] editTexts = new EditText[5];
    int [] editTextIDs = {R.id.editText_work_1,R.id.editText_work_2,R.id.editText_work_3,R.id.editText_work_4,R.id.editText_work_5};
    Profiles profiles;
    Button ok;
    Switch checkAll;
    boolean[] isCheckBoxChecked;
    String[] works;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list2);

        profiles = (Profiles)getIntent().getSerializableExtra("profile");
        isCheckBoxChecked = profiles.getToDoListEnabled();
        works = profiles.getWorks();

        if(profiles == null){
            Toast.makeText(getApplicationContext(),"Restart the application.",Toast.LENGTH_LONG).show();
            return;
        }

        ok = (Button)findViewById(R.id.okToDoList);
        checkAll = (Switch) findViewById(R.id.checkAll);

        for(int i = 0; i<checkBoxes.length;i++){
            checkBoxes[i] = (CheckBox)findViewById(checkvoxIDs[i]);
            editTexts[i] = (EditText)findViewById(editTextIDs[i]);
        }

        for (int i = 0; i<checkBoxes.length;i++){
            checkBoxes[i].setOnCheckedChangeListener(null);
            checkBoxes[i].setChecked(isCheckBoxChecked[i]);
            if(!TextUtils.isEmpty(works[i]))
                editTexts[i].setText(works[i]);
        }

        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for(int i = 0;i<isCheckBoxChecked.length;i++){
                    isCheckBoxChecked[i] = isChecked;
                    checkBoxes[i].setChecked(isChecked);
                }
            }
        });

        for(int i = 0;i<checkBoxes.length;i++){
            checkBoxes[i].setOnCheckedChangeListener(new customOncheckedChangeListener(i));
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean moveForward = true;
                for(int i = 0;i<editTexts.length;i++){
                    String text = editTexts[i].getText().toString().trim();
                    if(!TextUtils.isEmpty(text))
                        works[i] = text;
                    else
                        isCheckBoxChecked[i] = false;
                    if(text.length() > 50)
                        moveForward = false;
                }
                if(moveForward) {
                    Intent intent = new Intent();
                    intent.putExtra("isChecked", isCheckBoxChecked);
                    intent.putExtra("works", works);
                    setResult(1, intent);
                    finish();
                }
                else
                    Toast.makeText(ToDoList.this,"Keep your reminder short (within 40 characters)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class customOncheckedChangeListener implements CompoundButton.OnCheckedChangeListener{

        int position;
        customOncheckedChangeListener(int position){
            this.position = position;
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
           isCheckBoxChecked[position] = isChecked;
        }
    }
}
