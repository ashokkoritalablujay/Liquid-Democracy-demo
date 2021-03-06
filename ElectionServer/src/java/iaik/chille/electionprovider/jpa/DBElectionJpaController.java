/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iaik.chille.electionprovider.jpa;

import iaik.chille.electionprovider.db.DBElection;
import iaik.chille.electionprovider.jpa.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

/**
 *
 * @author chille
 */
public class DBElectionJpaController implements Serializable {

  public DBElectionJpaController(EntityManagerFactory emf) {
    this.emf = emf;
  }
  private EntityManagerFactory emf = null;

  public EntityManager getEntityManager() {
    return emf.createEntityManager();
  }

  public void create(DBElection DBElection) {
    EntityManager em = null;
    try {
      em = getEntityManager();
      em.getTransaction().begin();
      em.persist(DBElection);
      em.getTransaction().commit();
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  public void edit(DBElection DBElection) throws NonexistentEntityException, Exception {
    EntityManager em = null;
    try {
      em = getEntityManager();
      em.getTransaction().begin();
      DBElection = em.merge(DBElection);
      em.getTransaction().commit();
    } catch (Exception ex) {
      String msg = ex.getLocalizedMessage();
      if (msg == null || msg.length() == 0) {
        String id = DBElection.getId().toString();
        if (findDBElection(id) == null) {
          throw new NonexistentEntityException("The dBElection with id " + id + " no longer exists.");
        }
      }
      throw ex;
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  public void destroy(String id) throws NonexistentEntityException {
    EntityManager em = null;
    try {
      em = getEntityManager();
      em.getTransaction().begin();
      DBElection DBElection;
      try {
        DBElection = em.getReference(DBElection.class, id);
        DBElection.getId();
      } catch (EntityNotFoundException enfe) {
        throw new NonexistentEntityException("The DBElection with id " + id + " no longer exists.", enfe);
      }
      em.remove(DBElection);
      em.getTransaction().commit();
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  public List<DBElection> findDBElectionEntities() {
    return findDBElectionEntities(true, -1, -1);
  }

  public List<DBElection> findDBElectionEntities(int maxResults, int firstResult) {
    return findDBElectionEntities(false, maxResults, firstResult);
  }

  private List<DBElection> findDBElectionEntities(boolean all, int maxResults, int firstResult) {
    EntityManager em = getEntityManager();
    try {
      Query q = em.createQuery("select object(o) from DBElection as o");
      if (!all) {
        q.setMaxResults(maxResults);
        q.setFirstResult(firstResult);
      }
      return q.getResultList();
    } finally {
      em.close();
    }
  }

  public DBElection findDBElection(String id) {
    EntityManager em = getEntityManager();
    try {
      return em.find(DBElection.class, id);
    } finally {
      em.close();
    }
  }

  public int getDBElectionCount() {
    EntityManager em = getEntityManager();
    try {
      Query q = em.createQuery("select count(o) from DBElection as o");
      return ((Long) q.getSingleResult()).intValue();
    } finally {
      em.close();
    }
  }

}
