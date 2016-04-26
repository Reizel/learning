package dao;

import java.util.List;


import model.BaseObject;

public interface DaoLayer<B extends BaseObject> {

	public B get(int id);

	public List<B> getList();

	public void add(B obj);

	public void delete(B obj);

	public void update(B obj);

}
