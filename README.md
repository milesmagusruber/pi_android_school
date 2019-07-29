# PI-school 2019

## Week 2
- Multithreading & Networking
  - https://developer.android.com/guide/background/
  - https://developer.android.com/training/multiple-threads/
  - https://developer.android.com/training/basics/network-ops/
  - https://youtu.be/yyZh3ME7Jyk 
  - https://guides.codepath.com/android/Handling-ProgressBars
  - https://guides.codepath.com/android/Consuming-APIs-with-Retrofit

 - Задания и codelabs
   - https://codelabs.developers.google.com/codelabs/android-training-create-asynctask/index.html?index=..%2F..android-training#0
   - Создать приложение которое будет контактировать с Flickr Api ( https://www.flickr.com/services/api/ ) 
   - При открытии приложения вы должны сначала демонстрировать лого приложения и названия (по сути Splash Screen)
   - После  Splash screen пользователь должен увидеть экран с полем для ввода текста и кнопкой Search
   - По нажатии на кнопку Search выполрнять поиск по Flickr (https://www.flickr.com/services/api/flickr.photos.search.html) с      заданым текстом
   - Полученный результат вывести в TextView который будет расположен ниже
   - Из полученного ответа от сервера вы должны сформировать прямую ссылку на каждую фотографию которая прийдет и сформировать строку, где каждая ссылка будет начинаться с новой строки
   - Отобразить полученный результат в TextView ( в случае если результатов много - текст в TextView должен скроллиться)
   - Пользователь должен быть в состоянии кликнуть по каждой ссылке (подсказка см. класс Linkify)
   - При клике на линку пользователь должен перейти на другой экран в приложении где он сможет просмотреть содержимое линки (по сути отобразить в Webview)
   - Не использовать для отображения список
