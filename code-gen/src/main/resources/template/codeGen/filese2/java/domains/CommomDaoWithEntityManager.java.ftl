package ${package};

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.linzi.framework.db.BaseCommomDaoEntityManager;

/**
 * <pre>
 * 数据库用的通用repo， 为这个包下面的所有domain服务
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
@Repository
public class CommomDaoWithEntityManager extends BaseCommomDaoEntityManager {

	@Override
	protected EntityManager getEntityManager() {
		return this.entityManager;
	}

	@PersistenceContext
	private EntityManager entityManager;
}
