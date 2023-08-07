package kr.co.moneybridge.core.annotation;

import kr.co.moneybridge.model.reservation.StyleStyle;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static kr.co.moneybridge.core.util.EnumUtil.isValidStyleStyle;

public class StyleStylesValidator implements ConstraintValidator<ValidStyleStyles, List<StyleStyle>> {

    @Override
    public boolean isValid(List<StyleStyle> styleList, ConstraintValidatorContext context) {
        // 상담 후기 스타일은 최소 1개, 최대 4개까지 입력 가능
        if (styleList == null || styleList.isEmpty() || styleList.size() > 4) {
            return false;
        }

        for (StyleStyle style : styleList) {
            if (style == null || !isValidStyleStyle(style)) {
                return false;
            }
        }

        return true;
    }
}
