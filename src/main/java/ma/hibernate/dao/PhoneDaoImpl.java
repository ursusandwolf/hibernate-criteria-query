package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
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

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);

            Predicate predicate = cb.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                if (values.length == 1) {
                    predicate = cb.and(cb.equal(root.get(key), values[0]));
                } else {
                    Predicate p2 = cb.and();
                    for (String value : values) {
                        p2 = cb.or(cb.equal(root.get(key), value));
                    }
                    predicate = cb.and(predicate, p2);
                }
            }
            query.where(cb.and(predicate));
            return session.createQuery(query).getResultList();
        }
    }
}

