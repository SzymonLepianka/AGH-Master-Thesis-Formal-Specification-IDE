package sl.fside.ui.editors.activityDiagramEditor.managers;


public class NodesManager {
    private static NodesManager instance;
//    private final List<Tank> allTanks = new ArrayList<>();

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
