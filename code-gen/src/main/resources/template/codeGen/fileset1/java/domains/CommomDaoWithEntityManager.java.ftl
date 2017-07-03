package ${package};

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.cfido.commons.utils.db.BaseCommomDaoEntityManager;

/**
 * <pre>
 * 数据库用的通用repo， 为这个包下面的所有domain服务，
 * Spring boot在引入了jpa依赖后，自然就有EntityManager，无需做任何设置。所以这个类非常简单。
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

	/**
	* <pre>
	* 除非用到了多个数据源，否则这里无需修改，Spring boot自动搞定一切，而我们的项目一般都是单数据源的
	* 如果用到了多个数据库，需要设置 @PersistenceContext(unitName = 指定数据源的EntityManager名字)
	* </pre>
	*/
	@PersistenceContext
	private EntityManager entityManager;
}
