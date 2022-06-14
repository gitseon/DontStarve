package com.example.dontstarve.src.ingredient;

import com.example.dontstarve.config.BaseException;
import com.example.dontstarve.config.BaseResponse;
import com.example.dontstarve.config.BaseResponsePaging;
import com.example.dontstarve.config.PagingRes;
import com.example.dontstarve.src.ingredient.model.GetIngredientsRes;
import com.example.dontstarve.src.ingredient.model.IngredientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.dontstarve.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {

        this.ingredientService = ingredientService;
    }

    /*public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }*/

    /**
     * 식재료 저장 API
     * [POST] /ingredients?userId=
     * idx 반환
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<Integer> saveIngredient(@RequestParam("userId") int userId,
                                                @RequestBody IngredientDto ingredientDto) {
        int ingredientId = ingredientService.save(ingredientDto, userId);
        if (ingredientId == -1) { // 유저 존재하지 않으면 오류 메시지
            return new BaseResponse<>(NOT_EXISTS_USER);
        }
        return new BaseResponse<>(ingredientId);
    }



    /**
     * 식재료 조회 API
     * [GET] /ingredients?userId=&pageNum=
     * 식재료 정보 반환
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponsePaging<List<GetIngredientsRes>> findIngredient(@RequestParam("userId") int userId, Pageable pageable) throws BaseException {
        try {
            return ingredientService.getIngredients(userId, pageable);
        } catch (BaseException exception) {
            return new BaseResponsePaging<>(exception.getStatus());
        }
    }


    /**
     * 식재료 삭제 API
     * [PATCH] /ingredients
     * 식재료 정보 반환
     */
}
