package com.sjcdigital.model.repositories;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

/**
 *
 * Uma classe Repositorio abstrata para uso com as entidades do nosso sistema
 *
 * @author Pedro Hos
 * @author william
 *
 */
@Stateless
public class DefaultRepository<T> {

	protected Class<T> tipo = retornaTipo();

	@PersistenceContext(unitName = "obras-ds")
	protected EntityManager em;

	public void novo(T entidade) {
		em.persist(entidade);
	}

	public void remover(T entidade) {
		em.remove(entidade);
	}

	@SuppressWarnings("unchecked")
	public List<T> todos() {
		CriteriaQuery<Object> cq = em.getCriteriaBuilder().createQuery();
		cq.select(cq.from(tipo));
		return (List<T>) em.createQuery(cq).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> todosPaginado(int total, int pg) {
		CriteriaQuery<Object> cq = em.getCriteriaBuilder().createQuery();
		cq.select(cq.from(tipo));
		Query busca = em.createQuery(cq);
		busca.setFirstResult(pg * total);
		busca.setMaxResults(total);
		return (List<T>) busca.getResultList();
	}	
	
	public T comId(long id) {
		return em.find(tipo, id);
	}

	public T atualizar(T entidade) {
		return em.merge(entidade);
	}

	/**
	 * @author Pedro Hos<br>
	 *
	 *         Utilizando Exemplo de Eduardo Guerra!
	 *         https://groups.google.com/forum/#!topic/projeto-oo-guiado-por-
	 *         padroes/pOIiOD9cifs
	 *
	 *         Este método retorna o tipo da Classe, dessa maneira não é
	 *         necessário cada Service expor seu tipo!!!!
	 *
	 * @return Class<T>
	 */
	@SuppressWarnings({ "unchecked" })
	private Class<T> retornaTipo() {
		Class<?> clazz = this.getClass();

		while (!clazz.getSuperclass().equals(DefaultRepository.class)) {
			clazz = clazz.getSuperclass();
		}

		ParameterizedType tipoGenerico = (ParameterizedType) clazz.getGenericSuperclass();
		return (Class<T>) tipoGenerico.getActualTypeArguments()[0];
	}

}
