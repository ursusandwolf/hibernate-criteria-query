package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.protobuf.MapEntry;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();

        session.persist(phone); //or session.save(phone)

        transaction.commit();
        session.close();
        return phone;
    }

    /**
     * Map<String, String[]> params = new HashMap<>();
     * params.put("countryManufactured", new String[]{"China"};
     * params.put("maker", new String[]{"apple", "nokia", "samsung"};
     * params.put("color", new String[]{"white", "red"};
     */
    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        Session session = factory.openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
        Root<Phone> root = query.from(Phone.class);
        List<Phone> resultList = new ArrayList<>();
        Set<Map.Entry<String, String[]>> entrySet = params.entrySet();
        for (Map.Entry<String, String[]> entry : entrySet) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            for (String value : values) {
                query.where(criteriaBuilder.equal(root.get(key), value));
                resultList.addAll(session.createQuery(query).getResultList());
            }
        }
        return resultList;
    }
    /**
     * // SELECT * FROM Phone as p WHERE p.id = 1
     * query.where(criteriaBuilder.equal(root.get("id"), 1));
     * Phone phone = session.createQuery(query).uniqueResult();
     */
}
