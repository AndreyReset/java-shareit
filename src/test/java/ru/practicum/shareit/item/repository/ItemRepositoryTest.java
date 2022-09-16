package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@DataJpaTest
@TestPropertySource("/application-test.properties")
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    TestEntityManager em;

    private List<Item> list = List.of(
            new Item(1L, "Отвёртка", "Крестовая отвёртка", true),
            new Item(3L, "Отвёртка", "Очень ржавая", true)
    );

    @Test
    public void findItemsWhichContainsTextInNameOrDescription() {
        List<Item> items = itemRepository.findItemsByNameAndDescriptionAndAvailable(
                                "Отвёртка",
                                PageRequest.of(0, 100));

        assertThat(list.size(), equalTo(items.size()));
        assertThat(list.get(0).getName(), equalTo(items.get(0).getName()));
    }

    @Test
    public void findItemsWhichNotContainsTextInNameOrDescription() {
        List<Item> items = itemRepository.findItemsByNameAndDescriptionAndAvailable(
                "Ножовка",
                PageRequest.of(0, 100));

        assertThat(0, equalTo(items.size()));
    }
}