#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <QFile>
#include <QTextStream>
#include <QMessageBox>
#include <QProcess>
#include <QDebug>

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    //Słownik danych
    proverDict.insert("kazdego", "all");
    proverDict.insert("wszystkie", "all");
    proverDict.insert("istnieje", "exists");
    proverDict.insert("i", "&");
    proverDict.insert("lub", "|");
    proverDict.insert("nieprawda", "-");
    proverDict.insert("jest", "->");
    proverDict.insert("to", "->");
    proverDict.insert("rownowazne", "<->");
    proverDict.insert("rowna", "=");
    proverDict.insert(".", ".\n");
    proverDict.insert("formula(zalozenia)", "formulas(assumptions)");
    proverDict.insert("formula(cele)", "formulas(goals)");
    proverDict.insert("koniec", "end_of_list");

    //Do usuwania
    proverDict.insert("dla", "");
    proverDict.insert("ze", "");
    proverDict.insert("jezeli", "");
    proverDict.insert("sie", "");
    proverDict.insert("taki", "");
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::input_compare(QStringList splitFormulaUserInput)
{
    QString translateFormulaInput;

    //Szukanie tlumaczenia
    for(int i=0; i<splitFormulaUserInput.length(); i++) {
            if(proverDict.contains(splitFormulaUserInput.at(i)))
                translateFormulaInput = translateFormulaInput +" "+ proverDict.value(splitFormulaUserInput.at(i));
            else
                translateFormulaInput = translateFormulaInput+" "+splitFormulaUserInput.at(i);
    }

    ui->proverInput->setText(translateFormulaInput);
}

void MainWindow::on_makeInputFile_clicked()
{
    QFile file("prover_input.in");
    file.open(QIODevice::WriteOnly);
    QTextStream out(&file);
    out << ui->proverInput->toPlainText();
    file.close();
}

void MainWindow::on_runProver_clicked()
{
    //uruchomienie provera
    QProcess proc;
    QString program = "prover9";
    QStringList arguments;
    arguments << "-f" << "prover_input.in";

    //odczytanie z konsoli
    proc.start(program, arguments);
    proc.waitForFinished();
    QString output(proc.readAllStandardOutput());

    //zapis wyników do pliku
    QFile file("prover_output.out");
    file.open(QIODevice::WriteOnly);
    QTextStream out(&file);
    out << output;
    file.close();

    //wyswietlenie w programie
    if(ui->onlyProof->isChecked())
    {
        QProcess process;
        QString program = "prooftrans";
        QStringList arguments;
        arguments << "-f" << "prover_output.out";

        process.start(program, arguments);
        process.waitForFinished();
        QString outputProof(process.readAllStandardOutput());

        ui->proverOutput->setText(outputProof);
        proc.kill();
    } else {
        ui->proverOutput->setText(output);
        proc.kill();
    }
}

void MainWindow::on_showExample_clicked()
{
    ui->formula->setText("formula(zalozenia) .\njezeli czlowiek(x) jest smiertelny(x) .\nczlowiek(sokrates) .\nkoniec .\nformula(cele) .\nsmiertelny(sokrates) .\nkoniec .");
}

void MainWindow::on_formula_textChanged()
{
    //Pobranie Formuly
    QString userFormulaInput = ui->formula->toPlainText();
    userFormulaInput.replace("\n", " ");
    QStringList splitFormulaUserInput = userFormulaInput.split(" ");
    input_compare(splitFormulaUserInput);
}
