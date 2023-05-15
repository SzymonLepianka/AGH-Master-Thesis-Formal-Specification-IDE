#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QMap>

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();

private slots:

    void on_runProver_clicked();

    void input_compare(QStringList);

    void on_makeInputFile_clicked();

    void on_showExample_clicked();

    void on_formula_textChanged();

private:
    Ui::MainWindow *ui;
    QMap<QString, QString> proverDict;
};

#endif // MAINWINDOW_H
