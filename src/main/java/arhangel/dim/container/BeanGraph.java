package arhangel.dim.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.ArrayList;

/**
 *
 */
public class BeanGraph {
    // Граф представлен в виде списка связности для каждой вершины
    private Map<BeanVertex, List<BeanVertex>> vertices = new HashMap<>();
    private List<BeanVertex> grayBeans = new ArrayList<>();
    private List<BeanVertex> blackBeans = new ArrayList<>();

    /**
     * Добавить вершину в граф
     *
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex vertex = new BeanVertex(value);
        this.vertices.put(vertex, new ArrayList<>());
        return vertex;
    }

    /**
     * Соединить вершины ребром
     *
     * @param from из какой вершины
     * @param to   в какую вершину
     */
    public void addEdge(BeanVertex from, BeanVertex to) {
        this.vertices.get(from).add(to);
    }

    /**
     * Проверяем, связаны ли вершины
     */
    public boolean isConnected(BeanVertex parent, BeanVertex child) {
        Map<String, Property> properties = parent.getBean().getProperties();
        if (properties.size() > 0) {
            for (String propertyName : properties.keySet()) {
                Property property = properties.get(propertyName);
                if (property.getType() == ValueType.REF && property.getValue() == child.getBean().getName()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Получить список вершин, с которыми связана vertex
     */
    public List<BeanVertex> getLinked(BeanVertex vertex) {
        return this.vertices.get(vertex);
    }

    /**
     * Количество вершин в графе
     */
    public int size() {
        if (this.vertices.isEmpty()) {
            return 0;
        }
        return this.vertices.size();
    }

    /**
     * мой личный метод
     */
    public List<BeanVertex> sort() {
        try {
            List<BeanVertex> sortedBeanVertexes = new ArrayList<>();
            for (BeanVertex v : this.vertices.keySet()) {
                if (!this.blackBeans.contains(v)) {
                    this.sortStage(v);
                    this.blackBeans.add(v);
                }
            }
            for (BeanVertex bean : blackBeans) {
                sortedBeanVertexes.add(bean);
            }
            return sortedBeanVertexes;
        } catch (Exception e) {
            return null;
        }
    }

    private void sortStage(BeanVertex vertex) throws Exception {
        this.grayBeans.add(vertex);
        if (!this.vertices.get(vertex).isEmpty()) {
            for (BeanVertex v : this.vertices.get(vertex)) {
                if (this.grayBeans.contains(v)) {
                    throw new Exception("cycle detected!");
                }
                if (!this.blackBeans.contains(v)) {
                    this.sortStage(v);
                    this.grayBeans.remove(v);
                    this.blackBeans.add(v);
                }
            }
        }
    }

}
