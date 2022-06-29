package com.theagilemonkeys.crm.domain.service;

public interface CrudService<T> extends Creator<T>, Finder<T>, Updater<T>, Deleter<T> {
}
