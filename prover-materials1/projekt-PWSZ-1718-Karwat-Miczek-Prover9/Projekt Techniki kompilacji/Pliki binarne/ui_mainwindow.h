/********************************************************************************
** Form generated from reading UI file 'mainwindow.ui'
**
** Created by: Qt User Interface Compiler version 5.9.2
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_MAINWINDOW_H
#define UI_MAINWINDOW_H

#include <QtCore/QVariant>
#include <QtWidgets/QAction>
#include <QtWidgets/QApplication>
#include <QtWidgets/QButtonGroup>
#include <QtWidgets/QCheckBox>
#include <QtWidgets/QGridLayout>
#include <QtWidgets/QHBoxLayout>
#include <QtWidgets/QHeaderView>
#include <QtWidgets/QLabel>
#include <QtWidgets/QMainWindow>
#include <QtWidgets/QMenuBar>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QStatusBar>
#include <QtWidgets/QTextBrowser>
#include <QtWidgets/QTextEdit>
#include <QtWidgets/QToolBar>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_MainWindow
{
public:
    QWidget *centralWidget;
    QGridLayout *gridLayout;
    QGridLayout *gridLayout_2;
    QTextEdit *formula;
    QLabel *input;
    QTextBrowser *proverOutput;
    QLabel *label_2;
    QTextBrowser *proverInput;
    QLabel *label;
    QHBoxLayout *horizontalLayout;
    QPushButton *showExample;
    QPushButton *clear;
    QHBoxLayout *horizontalLayout_2;
    QPushButton *runProver;
    QCheckBox *onlyProof;
    QPushButton *makeInputFile;
    QMenuBar *menuBar;
    QToolBar *mainToolBar;
    QStatusBar *statusBar;

    void setupUi(QMainWindow *MainWindow)
    {
        if (MainWindow->objectName().isEmpty())
            MainWindow->setObjectName(QStringLiteral("MainWindow"));
        MainWindow->resize(823, 439);
        centralWidget = new QWidget(MainWindow);
        centralWidget->setObjectName(QStringLiteral("centralWidget"));
        gridLayout = new QGridLayout(centralWidget);
        gridLayout->setSpacing(6);
        gridLayout->setContentsMargins(11, 11, 11, 11);
        gridLayout->setObjectName(QStringLiteral("gridLayout"));
        gridLayout_2 = new QGridLayout();
        gridLayout_2->setSpacing(6);
        gridLayout_2->setObjectName(QStringLiteral("gridLayout_2"));
        formula = new QTextEdit(centralWidget);
        formula->setObjectName(QStringLiteral("formula"));
        formula->setMinimumSize(QSize(400, 0));

        gridLayout_2->addWidget(formula, 3, 0, 1, 1);

        input = new QLabel(centralWidget);
        input->setObjectName(QStringLiteral("input"));

        gridLayout_2->addWidget(input, 1, 1, 1, 1);

        proverOutput = new QTextBrowser(centralWidget);
        proverOutput->setObjectName(QStringLiteral("proverOutput"));

        gridLayout_2->addWidget(proverOutput, 3, 3, 1, 1);

        label_2 = new QLabel(centralWidget);
        label_2->setObjectName(QStringLiteral("label_2"));

        gridLayout_2->addWidget(label_2, 1, 0, 1, 1);

        proverInput = new QTextBrowser(centralWidget);
        proverInput->setObjectName(QStringLiteral("proverInput"));

        gridLayout_2->addWidget(proverInput, 3, 1, 1, 1);

        label = new QLabel(centralWidget);
        label->setObjectName(QStringLiteral("label"));

        gridLayout_2->addWidget(label, 1, 3, 1, 1);

        horizontalLayout = new QHBoxLayout();
        horizontalLayout->setSpacing(6);
        horizontalLayout->setObjectName(QStringLiteral("horizontalLayout"));
        showExample = new QPushButton(centralWidget);
        showExample->setObjectName(QStringLiteral("showExample"));

        horizontalLayout->addWidget(showExample);

        clear = new QPushButton(centralWidget);
        clear->setObjectName(QStringLiteral("clear"));

        horizontalLayout->addWidget(clear);


        gridLayout_2->addLayout(horizontalLayout, 0, 0, 1, 1);

        horizontalLayout_2 = new QHBoxLayout();
        horizontalLayout_2->setSpacing(6);
        horizontalLayout_2->setObjectName(QStringLiteral("horizontalLayout_2"));
        runProver = new QPushButton(centralWidget);
        runProver->setObjectName(QStringLiteral("runProver"));

        horizontalLayout_2->addWidget(runProver);

        onlyProof = new QCheckBox(centralWidget);
        onlyProof->setObjectName(QStringLiteral("onlyProof"));

        horizontalLayout_2->addWidget(onlyProof);


        gridLayout_2->addLayout(horizontalLayout_2, 0, 3, 1, 1);

        makeInputFile = new QPushButton(centralWidget);
        makeInputFile->setObjectName(QStringLiteral("makeInputFile"));

        gridLayout_2->addWidget(makeInputFile, 0, 1, 1, 1);


        gridLayout->addLayout(gridLayout_2, 1, 2, 1, 1);

        MainWindow->setCentralWidget(centralWidget);
        menuBar = new QMenuBar(MainWindow);
        menuBar->setObjectName(QStringLiteral("menuBar"));
        menuBar->setGeometry(QRect(0, 0, 823, 22));
        MainWindow->setMenuBar(menuBar);
        mainToolBar = new QToolBar(MainWindow);
        mainToolBar->setObjectName(QStringLiteral("mainToolBar"));
        MainWindow->addToolBar(Qt::TopToolBarArea, mainToolBar);
        statusBar = new QStatusBar(MainWindow);
        statusBar->setObjectName(QStringLiteral("statusBar"));
        MainWindow->setStatusBar(statusBar);

        retranslateUi(MainWindow);
        QObject::connect(clear, SIGNAL(clicked()), formula, SLOT(clear()));
        QObject::connect(clear, SIGNAL(clicked()), proverInput, SLOT(clear()));
        QObject::connect(clear, SIGNAL(clicked()), proverOutput, SLOT(clear()));

        QMetaObject::connectSlotsByName(MainWindow);
    } // setupUi

    void retranslateUi(QMainWindow *MainWindow)
    {
        MainWindow->setWindowTitle(QApplication::translate("MainWindow", "PWSZ Prover9 Insert", Q_NULLPTR));
        input->setText(QApplication::translate("MainWindow", "Input", Q_NULLPTR));
        label_2->setText(QApplication::translate("MainWindow", "Formula:", Q_NULLPTR));
        label->setText(QApplication::translate("MainWindow", "Output", Q_NULLPTR));
        showExample->setText(QApplication::translate("MainWindow", "Show Example", Q_NULLPTR));
        clear->setText(QApplication::translate("MainWindow", "Clear", Q_NULLPTR));
        runProver->setText(QApplication::translate("MainWindow", "Run Prover", Q_NULLPTR));
        onlyProof->setText(QApplication::translate("MainWindow", "Proof only", Q_NULLPTR));
        makeInputFile->setText(QApplication::translate("MainWindow", "Make Input File", Q_NULLPTR));
    } // retranslateUi

};

namespace Ui {
    class MainWindow: public Ui_MainWindow {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_MAINWINDOW_H
