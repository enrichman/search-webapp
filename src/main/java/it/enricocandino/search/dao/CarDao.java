package it.enricocandino.search.dao;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Enrico Candino
 */
@Document(collection = "car")
public class CarDao {

    private String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
