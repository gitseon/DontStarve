package com.example.dontstarve.src.ingredient;

import com.example.dontstarve.config.BaseException;
import com.example.dontstarve.config.BaseResponsePaging;
import com.example.dontstarve.config.PagingRes;
import com.example.dontstarve.src.ingredient.model.GetIngredientsRes;
import com.example.dontstarve.src.ingredient.model.IngredientDto;
import com.example.dontstarve.src.user.User;
import com.example.dontstarve.src.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.dontstarve.config.BaseResponseStatus.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository,
                             UserRepository userRepository) {
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
    }



    /*public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }*/

    // 식재료 저장
    public int save(IngredientDto ingredientDto, int userId){
        Optional<User> existing = userRepository.findById(userId); // 유저 존재 여부 확인
        if (!existing.isPresent()){ // 유저 존재 확인
            return -1;
        }

        Ingredient ingredient = Ingredient.builder() // 식재료 생성
                /*.userId(userId)*/
                .name(ingredientDto.getName())
                .type(ingredientDto.getType())
                .status("active")
                .user(existing.get())
                .build();

        return ingredientRepository.save(ingredient).getId(); // 식재료 id 반환
    }

    // 식재료 조회
    public BaseResponsePaging<List<GetIngredientsRes>> getIngredients(int userId, Pageable pageable) throws BaseException, NullPointerException{
        try {
            Optional<User> existing = userRepository.findById(userId); // 유저 존재 여부 확인
            if (!existing.isPresent()) { // 유저 존재 확인
                throw new BaseException(NOT_EXISTS_USER);
            }

            Page<Ingredient> pageInfo = ingredientRepository.findAllByUser(existing.get(), pageable); // 페이지 정보
            PagingRes pagingRes = new PagingRes(
                pageInfo.hasNext(),
                pageInfo.getNumber(),
                0,
                pageInfo.getTotalPages()-1,
                pageInfo.getSize(),
                pageInfo.getNumberOfElements(),
                pageInfo.getTotalElements()
            );

            List<Ingredient> ingredients = pageInfo.getContent();
            List<GetIngredientsRes> getIngredients = new ArrayList<>();

            for (Ingredient i : ingredients) {
                getIngredients.add(new GetIngredientsRes(i.getId(), i.getName(), i.getType()));
            }

            BaseResponsePaging<List<GetIngredientsRes>> paging = new BaseResponsePaging<>(
                    getIngredients, pagingRes
            );

            return paging;

            /*int pageNum = pageInfo.getCurrentPage();
            Long totalNum = 0L;

            List<GetIngredientsRes> result = new ArrayList<>();

            totalNum = ingredientRepository.countAllByUserId(userId);

            List<GetIngredientsRes> ingredients =
                    ingredientRepository.findAllByUserId(pageable, userId);

            return ingredients;*/
        } catch (BaseException exception) {
            throw new BaseException(DATABASE_ERROR);
        } catch (NullPointerException nullPointerException) {
            throw new BaseException(EMPTY_RESULT);
        }
    }
}
