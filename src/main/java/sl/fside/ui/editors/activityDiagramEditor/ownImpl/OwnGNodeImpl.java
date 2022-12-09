package sl.fside.ui.editors.activityDiagramEditor.ownImpl;

import io.github.eckig.grapheditor.model.*;
import org.eclipse.emf.common.notify.*;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.impl.*;
import org.eclipse.emf.ecore.util.*;

import java.util.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>GNode</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link OwnGNodeImpl#getId <em>Id</em>}</li>
 *   <li>{@link OwnGNodeImpl#getType <em>Type</em>}</li>
 *   <li>{@link OwnGNodeImpl#getX <em>X</em>}</li>
 *   <li>{@link OwnGNodeImpl#getY <em>Y</em>}</li>
 *   <li>{@link OwnGNodeImpl#getWidth <em>Width</em>}</li>
 *   <li>{@link OwnGNodeImpl#getHeight <em>Height</em>}</li>
 *   <li>{@link OwnGNodeImpl#getConnectors <em>Connectors</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OwnGNodeImpl extends MinimalEObjectImpl.Container implements GNode {
    /**
     * The default value of the '{@link #getId() <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getId()
     */
    protected static final String ID_EDEFAULT = null;
    /**
     * The default value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getType()
     */
    protected static final String TYPE_EDEFAULT = null;
    /**
     * The default value of the '{@link #getX() <em>X</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getX()
     */
    protected static final double X_EDEFAULT = 0.0;
    /**
     * The default value of the '{@link #getY() <em>Y</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getY()
     */
    protected static final double Y_EDEFAULT = 0.0;
    /**
     * The default value of the '{@link #getWidth() <em>Width</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getWidth()
     */
    protected static final double WIDTH_EDEFAULT = 151.0;
    /**
     * The default value of the '{@link #getHeight() <em>Height</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getHeight()
     */
    protected static final double HEIGHT_EDEFAULT = 101.0;
    /**
     * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getId()
     */
    protected String id = ID_EDEFAULT;
    /**
     * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getType()
     */
    protected String type = TYPE_EDEFAULT;
    /**
     * The cached value of the '{@link #getX() <em>X</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getX()
     */
    protected double x = X_EDEFAULT;
    /**
     * The cached value of the '{@link #getY() <em>Y</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getY()
     */
    protected double y = Y_EDEFAULT;
    /**
     * The cached value of the '{@link #getWidth() <em>Width</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getWidth()
     */
    protected double width = WIDTH_EDEFAULT;
    /**
     * The cached value of the '{@link #getHeight() <em>Height</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getHeight()
     */
    protected double height = HEIGHT_EDEFAULT;

    /**
     * The cached value of the '{@link #getConnectors() <em>Connectors</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     * @ordered
     * @see #getConnectors()
     */
    protected EList<GConnector> connectors;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public OwnGNodeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return GraphPackage.Literals.GNODE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void setId(String newId) {
        String oldId = id;
        id = newId;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, GraphPackage.GNODE__ID, oldId, id));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void setType(String newType) {
        String oldType = type;
        type = newType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, GraphPackage.GNODE__TYPE, oldType, type));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public double getX() {
        return x;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void setX(double newX) {
        double oldX = x;
        x = newX;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, GraphPackage.GNODE__X, oldX, x));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public double getY() {
        return y;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void setY(double newY) {
        double oldY = y;
        y = newY;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, GraphPackage.GNODE__Y, oldY, y));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public double getWidth() {
        return width;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void setWidth(double newWidth) {
        double oldWidth = width;
        width = newWidth;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, GraphPackage.GNODE__WIDTH, oldWidth, width));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public double getHeight() {
        return height;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void setHeight(double newHeight) {
        double oldHeight = height;
        height = newHeight;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, GraphPackage.GNODE__HEIGHT, oldHeight, height));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public EList<GConnector> getConnectors() {
        if (connectors == null) {
            connectors = new EObjectContainmentWithInverseEList<>(GConnector.class, this, GraphPackage.GNODE__CONNECTORS, GraphPackage.GCONNECTOR__PARENT);
        }
        return connectors;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        if (featureID == GraphPackage.GNODE__CONNECTORS) {
            return ((InternalEList<InternalEObject>) (InternalEList<?>) getConnectors()).basicAdd(otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        if (featureID == GraphPackage.GNODE__CONNECTORS) {
            return ((InternalEList<?>) getConnectors()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        return switch (featureID) {
            case GraphPackage.GNODE__ID -> getId();
            case GraphPackage.GNODE__TYPE -> getType();
            case GraphPackage.GNODE__X -> getX();
            case GraphPackage.GNODE__Y -> getY();
            case GraphPackage.GNODE__WIDTH -> getWidth();
            case GraphPackage.GNODE__HEIGHT -> getHeight();
            case GraphPackage.GNODE__CONNECTORS -> getConnectors();
            default -> super.eGet(featureID, resolve, coreType);
        };
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case GraphPackage.GNODE__ID -> {
                setId((String) newValue);
                return;
            }
            case GraphPackage.GNODE__TYPE -> {
                setType((String) newValue);
                return;
            }
            case GraphPackage.GNODE__X -> {
                setX((Double) newValue);
                return;
            }
            case GraphPackage.GNODE__Y -> {
                setY((Double) newValue);
                return;
            }
            case GraphPackage.GNODE__WIDTH -> {
                setWidth((Double) newValue);
                return;
            }
            case GraphPackage.GNODE__HEIGHT -> {
                setHeight((Double) newValue);
                return;
            }
            case GraphPackage.GNODE__CONNECTORS -> {
                getConnectors().clear();
                getConnectors().addAll((Collection<? extends GConnector>) newValue);
                return;
            }
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case GraphPackage.GNODE__ID -> {
                setId(ID_EDEFAULT);
                return;
            }
            case GraphPackage.GNODE__TYPE -> {
                setType(TYPE_EDEFAULT);
                return;
            }
            case GraphPackage.GNODE__X -> {
                setX(X_EDEFAULT);
                return;
            }
            case GraphPackage.GNODE__Y -> {
                setY(Y_EDEFAULT);
                return;
            }
            case GraphPackage.GNODE__WIDTH -> {
                setWidth(WIDTH_EDEFAULT);
                return;
            }
            case GraphPackage.GNODE__HEIGHT -> {
                setHeight(HEIGHT_EDEFAULT);
                return;
            }
            case GraphPackage.GNODE__CONNECTORS -> {
                getConnectors().clear();
                return;
            }
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        return switch (featureID) {
            case GraphPackage.GNODE__ID -> ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
            case GraphPackage.GNODE__TYPE -> TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
            case GraphPackage.GNODE__X -> x != X_EDEFAULT;
            case GraphPackage.GNODE__Y -> y != Y_EDEFAULT;
            case GraphPackage.GNODE__WIDTH -> width != WIDTH_EDEFAULT;
            case GraphPackage.GNODE__HEIGHT -> height != HEIGHT_EDEFAULT;
            case GraphPackage.GNODE__CONNECTORS -> connectors != null && !connectors.isEmpty();
            default -> super.eIsSet(featureID);
        };
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        return super.toString() + " (id: " +
                id +
                ", type: " +
                type +
                ", x: " +
                x +
                ", y: " +
                y +
                ", width: " +
                width +
                ", height: " +
                height +
                ')';
    }

} //GNodeImpl
