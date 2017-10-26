package shander.testappsberbank.utils;

public interface IConverter<S, D> {

    D convert(S src);

}
