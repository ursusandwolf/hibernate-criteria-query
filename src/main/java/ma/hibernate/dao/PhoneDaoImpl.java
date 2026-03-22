package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
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
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Phone> query = builder.createQuery(Phone.class);
        Root<Phone> root = query.from(Phone.class);
        Set<Map.Entry<String, String[]>> entrySet = params.entrySet();
        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : entrySet) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values.length == 1) {
                predicates.add(builder.equal(root.get(key), values[0]));
            } else {
                for (String value : values) {
                    predicates.add(builder.equal(root.get(key), value));
                }
                predicates.add(builder.or(predicates.toArray(new Predicate[0])));
            }
        }
        query.where(builder.and(predicates.toArray(new Predicate[0])));
        return session.createQuery(query).getResultList();
    }
}

