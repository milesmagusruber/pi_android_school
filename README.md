# PI-school 2019

# Week 8
 - Fragments & System communication 
 
   - https://developer.android.com/guide/components/fragments всю секцию
   - https://guides.codepath.com/android/Fragment-Navigation-Drawer
   - https://guides.codepath.com/android/ViewPager-with-FragmentPagerAdapter
   - https://developer.android.com/guide/components/broadcasts
   - https://developer.android.com/training/basics/fragments/fragment-ui.html
 - Задания и codelabs
    - https://codelabs.developers.google.com/codelabs/android-training-broadcast-receivers/index.html?index=..%2F..android-training#0 
    - https://codelabs.developers.google.com/codelabs/advanced-android-training-fragments/index.html?index=..%2F..advanced-android-training#0
    - https://codelabs.developers.google.com/codelabs/advanced-android-training-fragment-communication/index.html?index=..%2F..advanced-android-training#0
    - Работаем на основане приложения с прошлой недели:
      - Переделываем наше приложение на master\detail flow  (пример в есть в одной из кодлаб)
      - Добавляем Navigation Drawer в котором будут находится секции которые мы сделали в прошлых работах: Maps, Favourites, etc.
      - Экраны переводим на фрагменты для того что бы при выборе секции динамически менять контент на главном экране
      - Оставляем только одну главную Activity, которая будет "хостить" в себе фрагменты
      - Добавляем автоматический запуск приложения после загрузки телефона
      - Если на телефоне меняется заряд батареи, то на каком бы фрагменте мы не находились показываем  toast сообщение с текущим зарядом 
      - Лочим ориентацию приложения только на портретный режим
