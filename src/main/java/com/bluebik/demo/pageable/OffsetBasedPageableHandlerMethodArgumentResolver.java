package com.bluebik.demo.pageable;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class OffsetBasedPageableHandlerMethodArgumentResolver implements PageableArgumentResolver {

    private static final SortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new SortHandlerMethodArgumentResolver();
    private SortArgumentResolver sortResolver;

    private static final String DEFAULT_QUALIFIER_DELIMITER = "_";


    private String qualifierDelimiter = DEFAULT_QUALIFIER_DELIMITER;

    public OffsetBasedPageableHandlerMethodArgumentResolver() {
        this((SortArgumentResolver) null);
    }

    public OffsetBasedPageableHandlerMethodArgumentResolver(SortArgumentResolver sortResolver) {
        this.sortResolver = sortResolver == null ? DEFAULT_SORT_RESOLVER : sortResolver;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return Pageable.class.equals(methodParameter.getParameterType());
    }

    @Override
    public Pageable resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        Pageable defaultOrFallback = new OffsetBasedPageRequest(0, 20);

        String offsetString = webRequest.getParameter(getParameterNameToUse("offset", methodParameter));
        String limitString = webRequest.getParameter(getParameterNameToUse("limit", methodParameter));

        int offset = StringUtils.hasText(offsetString) ? Integer.parseUnsignedInt(offsetString) : defaultOrFallback.getOffset();
        int limit = StringUtils.hasText(limitString) ? Integer.parseUnsignedInt(limitString) : defaultOrFallback.getPageSize();
        Sort sort = sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        sort = sort == null ? defaultOrFallback.getSort() : sort;

        return new OffsetBasedPageRequest(offset, limit, sort);

    }

    private boolean isOffsetAndLimitGiven(String offsetString, String limitString) {
        return StringUtils.hasText(offsetString) && StringUtils.hasText(limitString);
    }

    /**
     * Returns the name of the request parameter to find the {@link Pageable} information in. Inspects the given
     * {@link MethodParameter} for {@link Qualifier} present and prefixes the given source parameter name with it.
     *
     * @param source    the basic parameter name.
     * @param parameter the {@link MethodParameter} potentially qualified.
     * @return the name of the request parameter.
     */
    protected String getParameterNameToUse(String source, MethodParameter parameter) {

        StringBuilder builder = new StringBuilder();

        if (parameter != null && parameter.hasParameterAnnotation(Qualifier.class)) {
            builder.append(parameter.getParameterAnnotation(Qualifier.class).value());
            builder.append(qualifierDelimiter);
        }

        return builder.append(source).toString();
    }

}
