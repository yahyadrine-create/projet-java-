package dao;
 
import java.util.List;
 
public interface Idao<T> {
    boolean create(T o);
    boolean update(T o);
    boolean delete(int id);
    T getParId(int id);
    List<T> getTous();
}