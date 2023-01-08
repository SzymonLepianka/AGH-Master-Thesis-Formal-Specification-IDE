package sl.fside.ui.editors.activityDiagramEditor.managers;


import javafx.scene.*;

public class NodesManager {
    private static NodesManager instance;
//    private final List<Tank> allTanks = new ArrayList<>();

    private Node main;
    private String patternExpression;

    public String getPatternExpression() {
        return patternExpression;
    }

    public void setPatternExpression(String patternExpression) {
        this.patternExpression = patternExpression;
    }

    public String getMainName() {
        return mainName;
    }

    public void setMainName(String mainName) {
        this.mainName = mainName;
    }

    private String mainName;

    public Node getMain() {
        return main;
    }

    public void setMain(Node main) {
        this.main = main;
    }

    private String currentNodeType;

    public static NodesManager getInstance() {
        var result = instance;
        if (instance != null) {
            return result;
        }
        synchronized (NodesManager.class) {
            if (instance == null) {
                instance = new NodesManager();
            }
            return instance;
        }
    }

    public String getCurrentNodeType() {
        return currentNodeType;
    }

    public void setCurrentNodeType(String nodeType) {
        currentNodeType = nodeType;
    }

    //    public void addTank(Tank tank) {
//        synchronized (allTanks) {
//            allTanks.add(tank);
//        }
//    }
//
//    public void deleteTank(Tank tank) {
//        synchronized (allTanks) {
//            allTanks.remove(tank);
//        }
//    }
//
//    public List<Tank> getTanks() {
//        synchronized (allTanks) {
//            return allTanks;
//        }
//    }
}
