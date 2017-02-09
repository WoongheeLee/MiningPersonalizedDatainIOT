1. 사용 알고리즘
=============================

아래 사용 알고리즘은 코사인 유사도와 매트릭스 팩토라이제이션을 구현하며 성능을 향상 시키기 위해 두 알고리즘을 병합하는 과정 순서대로 서술합니다

1), 2), 3), 4)에는 공통적으로 u1.base가 학습 데이터, u1.test가 검사 데이터로 사용됐습니다.


1) 코사인 유사도
-------------------------
유사도 임계치를 0.9999999으로 했을 때, RMSE가 최소였습니다.

Minimum RMSE = 1.1167289108469234


2) 심플 매트릭스 팩토라이제이션
-------------------------
레이턴트 팩터 디멘션이 160일 때,

Minimum RMSE = 1.1748


3) 레이팅 평균 대비 사용자 바이어스, 아이템 바이어스 매트릭스 팩토라이제이션
-------------------------
(Koren., Y.의 Matrix Factorization Techniques for Recommender Systems 참고)

Minimum RMSE = 0.98


4) 위의 2), 3)의 병합 알고리즘
-------------------------
매트릭스 팩토라이제이션에서 P X Q로 만들어진 R^에는 음수 값이나 레이팅 범위를 벗어나는 값
이 발생하여 예측 성능을 떨어뜨림

이에 대한 해결책으로 레이팅 범위를 벗어나는 값은 코사인 유사도를 이용하여 예측함

향상된 최종 결과:

Minimum RMSE = 0.9791021059703677



2. 다양한 학습 데이터, 테스트 데이터의 비교를 통한 RMSE 확인
=============================

train    test    RMSE

u1.base    u1.test    0.9791021059703677

u2.base    u2.test    0.9700101615773516

u3.base    u3.test    0.9686236051438114

u4.base    u4.test    0.9635928495962502

u5.base    u5.test    0.9634477926750454



3.  사용한 라이브러리
=============================

행렬 계산의 편의를 위해(행렬곱, L-2 norm 계산) 자바의 행렬 라이브러리 Jama 1.0.3 버전을 사용했습니다.
