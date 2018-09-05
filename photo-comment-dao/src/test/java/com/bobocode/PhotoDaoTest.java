package com.bobocode;

import com.bobocode.dao.PhotoDao;
import com.bobocode.dao.PhotoDaoImpl;
import com.bobocode.model.Photo;
import com.bobocode.util.EntityManagerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static com.bobocode.util.PhotoTestDataGenerator.createListOfRandomPhotos;
import static com.bobocode.util.PhotoTestDataGenerator.createRandomPhoto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class PhotoDaoTest {
    private EntityManagerUtil emUtil;
    private PhotoDao photoDao;

    @BeforeEach
    public void setup() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("PhotoComments");
        emUtil = new EntityManagerUtil(entityManagerFactory);
        photoDao = new PhotoDaoImpl(entityManagerFactory);
    }

    @Test
    public void testSavePhoto() {
        Photo photo = createRandomPhoto();

        photoDao.save(photo);

        Photo fountPhoto = emUtil.performReturningWithinTx(entityManager -> entityManager.find(Photo.class, photo.getId()));
        assertThat(fountPhoto, equalTo(photo));
    }

    @Test
    public void testFindPhotoById() {
        Photo photo = createRandomPhoto();
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));

        Photo foundPhoto = photoDao.findById(photo.getId());

        assertThat(foundPhoto, equalTo(photo));
    }

    @Test
    public void testFindAllPhotos() {
        List<Photo> listOfRandomPhotos = createListOfRandomPhotos(5);
        emUtil.performWithinTx(entityManager -> listOfRandomPhotos.forEach(entityManager::persist));

        List<Photo> foundPhotos = photoDao.findAll();

        assertThat(foundPhotos, containsInAnyOrder(listOfRandomPhotos.toArray()));
    }

    @Test
    public void testRemovePhoto() {
        Photo photo = createRandomPhoto();
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));

        photoDao.remove(photo);

        Photo removedPhoto = emUtil.performReturningWithinTx(entityManager -> entityManager.find(Photo.class, photo.getId()));
        assertThat(removedPhoto, nullValue());
    }

    @Test
    public void testAddPhotoComment() {
        Photo photo = createRandomPhoto();
        emUtil.performWithinTx(entityManager -> entityManager.persist(photo));

        photoDao.addComment(photo, "Nice picture!");

        emUtil.performWithinTx(entityManager -> {
            Photo managedPhoto = entityManager.find(Photo.class, photo.getId());
            assertThat(managedPhoto.getComments(), containsInAnyOrder(photo.getComments().toArray()));
        });
    }
}
