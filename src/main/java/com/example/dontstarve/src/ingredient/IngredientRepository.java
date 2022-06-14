package com.example.dontstarve.src.ingredient;

import com.example.dontstarve.src.ingredient.model.GetIngredientsRes;
import com.example.dontstarve.src.ingredient.model.IngredientDto;
import com.example.dontstarve.src.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

   // 해당 유저의 식재료 조회
    Page<Ingredient> findAllByUser(User user, Pageable pageable);

}
