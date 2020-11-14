package com.example.cadastrarcontatos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String NOME_BD = "cadastro_contato";
    public static final String NOME_TABELA = "contato";
    public static final String CAMPO_ID = "_id";
    public static final String CAMPO_NOME = "nome";
    public static final String CAMPO_EMAIL = "email";
    public static final String[] CAMPOS_CONTATO = {CAMPO_ID, CAMPO_NOME, CAMPO_EMAIL};

    private EditText edtCodigo;
    private EditText edtNome;
    private EditText edtEmail;
    private SQLiteDatabase database;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtCodigo = findViewById(R.id.edtCodigo);
        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        database = openOrCreateDatabase(NOME_BD, Context.MODE_PRIVATE, null);

        database.execSQL("CREATE TABLE IF NOT EXISTS " + NOME_TABELA + " (" +
                CAMPO_ID + " INTEGER, " +
                CAMPO_NOME + " TEXT, " +
                CAMPO_EMAIL + " TEXT);");
    }

    private Cursor obterContatos() {
        return database.query(NOME_TABELA, CAMPOS_CONTATO, null, null, null, null, null);
    }

    public void limparCampos() {
        edtCodigo.setText("");
        edtNome.setText("");
        edtEmail.setText("");
    }

    public void onSalvarClick(View v) {
        String nome = edtNome.getText().toString();
        String email = edtEmail.getText().toString();
        String codigo = edtCodigo.getText().toString();
        if (nome.trim().isEmpty() || email.trim().isEmpty() || codigo.trim().isEmpty()) {
            Toast.makeText(this, "preencha os campos", Toast.LENGTH_SHORT).show();
        } else {
            ContentValues values = new ContentValues();
            values.put(CAMPO_NOME, nome);
            values.put(CAMPO_EMAIL, email);
            values.put(CAMPO_ID, codigo);

            if (database.query(NOME_TABELA, CAMPOS_CONTATO, CAMPO_ID + " = " + codigo, null, null, null, null).getCount() > 0) {
                values.remove(CAMPO_ID);
                database.update(NOME_TABELA, values, CAMPO_ID + " = " + codigo, null);
            } else {
                database.insert(NOME_TABELA, null, values);
            }
            limparCampos();
        }
    }

    private void carregarContato(String codigo) {
        Cursor c = database.query(NOME_TABELA, CAMPOS_CONTATO, CAMPO_ID + " = " + codigo, null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            edtCodigo.setText(c.getString(0));
            edtNome.setText(c.getString(1));
            edtEmail.setText(c.getString(2));
        } else {
            Toast.makeText(this, "registro nao encontrado", Toast.LENGTH_SHORT).show();
        }
        c.close();
    }

    public void onExcluirClick(View v) {
        String codigo = edtCodigo.getText().toString().trim();
        if (codigo.isEmpty()) {
            edtCodigo.requestFocus();
            Toast.makeText(this, "codigo obrigatorio", Toast.LENGTH_SHORT).show();
            return;
        }
        database.delete(NOME_TABELA, CAMPO_ID + " = " + codigo, null);
        limparCampos();
    }

    public void onCarregar(View v) {
        String codigo = edtCodigo.getText().toString().trim();
        if (codigo.isEmpty()) {
            edtCodigo.requestFocus();
            Toast.makeText(this, "codigo obrigatorio", Toast.LENGTH_SHORT).show();
            return;
        } else {
            carregarContato(codigo);
        }
    }

    public void onListarClick(View v) {
        Cursor cursor = obterContatos();

        while (cursor.moveToNext()) {
            Log.d("listar", "id: " + cursor.getString(0) + " nome: " + cursor.getString(1) + " email: " + cursor.getString(2));
        }
    }
}